package rgo.cloud.authentication.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.boot.service.sender.MailSenderStub;
import rgo.cloud.authentication.boot.storage.repository.ClientRepository;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.common.api.model.Role;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.CommonTest;
import rgo.cloud.security.config.util.Endpoint;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.common.api.util.JsonUtil.toJson;
import static rgo.cloud.common.api.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class AuthorizationRestControllerTest extends CommonTest {

    @Autowired
    private ClientRepository clientRepository;

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
                .andExpect(jsonPath("$.object.active", is(false)));
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
    public void confirmAccount() throws Exception {
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
                .andExpect(jsonPath("$.object.active", is(false)));

        Optional<Client> opt = clientRepository.findByMail(rq.getMail());
        assertTrue(opt.isPresent());
        assertFalse(opt.get().isActive());

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(opt.get().getEntityId()))
                .param("token", MailSenderStub.TOKEN))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()));

        Optional<Client> activated = clientRepository.findByMail(rq.getMail());
        assertTrue(activated.isPresent());
        assertTrue(activated.get().isActive());
    }

    @Test
    public void confirmAccount_clientIdOrTokenIsFake() throws Exception {
        long clientId = generateId();
        String token = randomString();
        String errorMessage = "The token was not found during activation.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }
}
