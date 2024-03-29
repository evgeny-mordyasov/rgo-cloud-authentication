package rgo.cloud.authentication.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.db.api.repository.ClientEntryFailedRepository;
import rgo.cloud.authentication.service.config.properties.ClientEntryFailedProperties;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.common.api.model.Role;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.WebTest;
import rgo.cloud.security.config.util.Endpoint;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.authentication.db.utils.EntityGenerator.*;
import static rgo.cloud.common.api.util.JsonUtil.toJson;
import static rgo.cloud.common.spring.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class AuthorizationRestControllerTest extends WebTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ConfirmationTokenRepository tokenRepository;

    @Autowired
    private ClientEntryFailedRepository clientEntryFailedRepository;

    @Autowired
    private ClientEntryFailedProperties properties;

    @BeforeEach
    public void setUp() {
        truncateTables();
        initMvc();
    }

    @Test
    public void signUp() throws Exception {
        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", notNullValue()))
                .andExpect(jsonPath("$.object.surname", is(rq.getSurname())))
                .andExpect(jsonPath("$.object.name", is(rq.getName())))
                .andExpect(jsonPath("$.object.patronymic", is(rq.getPatronymic())))
                .andExpect(jsonPath("$.object.mail", is(rq.getMail())))
                .andExpect(jsonPath("$.object.role", is(Role.USER.name())))
                .andExpect(jsonPath("$.object.verified", is(false)));
    }

    @Test
    public void signUp_mailAlreadyExist() throws Exception {
        String errorMessage = "Client by mail already exist.";
        Client saved = clientRepository.save(createRandomClient());

        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(saved.getMail())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.VIOLATES_CONSTRAINT.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void signIn_unauthorized() throws Exception {
        String errorMessage = "The request contains invalid user data.";

        AuthorizationSignInRequest rq = AuthorizationSignInRequest.builder()
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.UNAUTHORIZED.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void signIn_blocked() throws Exception {
        String errorMessage = "The client is blocked.";

        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();
        signUp(rq);

        Optional<Client> opt = clientRepository.findByMail(rq.getMail());
        assertTrue(opt.isPresent());
        assertFalse(opt.get().isVerified());

        Optional<ConfirmationToken> optToken = tokenRepository.findByClientId(opt.get().getEntityId());
        assertTrue(optToken.isPresent());

        confirmAccount(opt.get().getEntityId(), optToken.get().getToken());

        AuthorizationSignInRequest fakeData = AuthorizationSignInRequest.builder()
                .mail(rq.getMail())
                .password(randomString())
                .build();

        for (int i = 0; i < properties.getMaxFailedAttempts() + 1; i++) {
            mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                    .content(toJson(fakeData))
                    .contentType(JSON))
                    .andExpect(content().contentType(JSON))
                    .andExpect(jsonPath("$.status.code", is(StatusCode.UNAUTHORIZED.name())));
        }

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                .content(toJson(fakeData))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.BANNED.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));

        Optional<ClientEntryFailed> found = clientEntryFailedRepository.findByMail(rq.getMail());

        assertTrue(found.isPresent());
        assertEquals(0, found.get().getAttempts());
    }

    @Test
    public void confirmAccount() throws Exception {
        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();
        signUp(rq);

        Optional<Client> opt = clientRepository.findByMail(rq.getMail());
        assertTrue(opt.isPresent());
        assertFalse(opt.get().isVerified());

        Optional<ConfirmationToken> optToken = tokenRepository.findByClientId(opt.get().getEntityId());
        assertTrue(optToken.isPresent());

        confirmAccount(opt.get().getEntityId(), optToken.get().getToken());

        Optional<Client> activated = clientRepository.findByMail(rq.getMail());
        assertTrue(activated.isPresent());
        assertTrue(activated.get().isVerified());
    }

    @Test
    public void confirmAccount_manyClients() throws Exception {
        initClients();
        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();
        signUp(rq);

        Optional<Client> opt = clientRepository.findByMail(rq.getMail());
        assertTrue(opt.isPresent());
        assertFalse(opt.get().isVerified());

        Optional<ConfirmationToken> optToken = tokenRepository.findByClientId(opt.get().getEntityId());
        assertTrue(optToken.isPresent());

        confirmAccount(opt.get().getEntityId(), optToken.get().getToken());

        Optional<Client> activated = clientRepository.findByMail(rq.getMail());
        assertTrue(activated.isPresent());
        assertTrue(activated.get().isVerified());
    }

    private void initClients() {
        int clients = ThreadLocalRandom.current().nextInt(1, 25);

        IntStream.range(0, clients).forEach(i -> {
            try {
                AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                        .surname(randomString())
                        .name(randomString())
                        .patronymic(randomString())
                        .mail(randomString())
                        .password(randomString())
                        .build();

               signUp(rq);
            } catch (Exception ignored) {}
        });
    }

    private void signUp(AuthorizationSignUpRequest rq) throws Exception {
        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", notNullValue()))
                .andExpect(jsonPath("$.object.surname", is(rq.getSurname())))
                .andExpect(jsonPath("$.object.name", is(rq.getName())))
                .andExpect(jsonPath("$.object.patronymic", is(rq.getPatronymic())))
                .andExpect(jsonPath("$.object.mail", is(rq.getMail())))
                .andExpect(jsonPath("$.object.role", is(Role.USER.name())))
                .andExpect(jsonPath("$.object.verified", is(false)));
    }

    private void confirmAccount(Long clientId, String token) throws Exception {
        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()));
    }

    @Test
    public void confirmAccount_clientIdIsFake() throws Exception {
        long clientId = generateId();
        String token = randomString();
        String errorMessage = "The client was not found during verification.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void confirmAccount_tokenIsFake() throws Exception {
        String token = randomString();
        String errorMessage = "The token is invalid.";

        Client savedClient = clientRepository.save(createRandomClient());
        tokenRepository.save(createRandomFullConfirmationToken(savedClient));

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(savedClient.getEntityId()))
                .param("token", token))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ILLEGAL_STATE.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void confirmAccount_tokenIsExpired() throws Exception {
        String errorMessage = "The token is expired.";
        LocalDateTime expireDate = LocalDateTime.now()
                .minusHours(1);

        Client savedClient = clientRepository.save(createRandomClient());
        ConfirmationToken token = tokenRepository.save(createRandomFullConfirmationToken(savedClient, expireDate));

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(savedClient.getEntityId()))
                .param("token", token.getToken()))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ILLEGAL_STATE.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void sendToken() throws Exception {
        Client savedClient = clientRepository.save(createRandomClient());
        ConfirmationToken savedToken = tokenRepository.save(createRandomFullConfirmationToken(savedClient));

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SEND_TOKEN)
                .param("clientId", Long.toString(savedClient.getEntityId())))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()));

        Optional<ConfirmationToken> opt = tokenRepository.findByClientId(savedClient.getEntityId());
        assertTrue(opt.isPresent());
        assertNotEquals(savedToken.getToken(), opt.get().getToken());
    }

    @Test
    public void sendToken_clientIdIsFake() throws Exception {
        long clientId = generateId();
        String errorMessage = "The client not found by clientId.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SEND_TOKEN)
                .param("clientId", Long.toString(clientId)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void resetPassword() throws Exception {
        Client saved = clientRepository.save(createRandomClient());
        clientRepository.updateStatus(saved.getEntityId(), true);

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.RESET_PASSWORD)
                .param("mail", saved.getMail()))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()));

        Optional<Client> opt = clientRepository.findById(saved.getEntityId());
        assertTrue(opt.isPresent());
        assertNotEquals(saved.getPassword(), opt.get().getPassword());
    }

    @Test
    public void resetPassword_clientNotActivated() throws Exception {
        Client saved = clientRepository.save(createRandomClient());
        String errorMessage = "The client is not verified.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.RESET_PASSWORD)
                .param("mail", saved.getMail()))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.BANNED.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));

        Optional<Client> opt = clientRepository.findById(saved.getEntityId());
        assertTrue(opt.isPresent());
        assertEquals(saved.getPassword(), opt.get().getPassword());
    }
}
