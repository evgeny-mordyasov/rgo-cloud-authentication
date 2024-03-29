package rgo.cloud.authentication.boot.api.permit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.common.api.model.Role;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.WebTest;
import rgo.cloud.security.config.domain.ClientDetails;
import rgo.cloud.security.config.jwt.JwtProvider;
import rgo.cloud.security.config.jwt.properties.JwtProperties;
import rgo.cloud.security.config.util.Endpoint;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.common.api.util.JsonUtil.toJson;
import static rgo.cloud.common.spring.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class AuthorizationRestControllerPermitTest extends WebTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtProperties config;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup() {
        truncateTables();
        initSecurityMvc();
    }

    @Test
    public void signUp_success_anonymous() throws Exception {
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
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())));
    }

    @Test
    public void signUp_success_client() throws Exception {
        String jwt = createJwt(Role.USER);
        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())));
    }

    @Test
    public void signUp_success_admin() throws Exception {
        String jwt = createJwt(Role.ADMIN);
        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())));
    }

    @Test
    public void signIn_success_anonymous() throws Exception {
        AuthorizationSignInRequest rq = AuthorizationSignInRequest.builder()
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.UNAUTHORIZED.name())));
    }

    @Test
    public void signIn_success_client() throws Exception {
        String jwt = createJwt(Role.USER);
        AuthorizationSignInRequest rq = AuthorizationSignInRequest.builder()
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                .content(toJson(rq))
                .contentType(JSON)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.UNAUTHORIZED.name())));
    }

    @Test
    public void signIn_success_admin() throws Exception {
        String jwt = createJwt(Role.ADMIN);
        AuthorizationSignInRequest rq = AuthorizationSignInRequest.builder()
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                .content(toJson(rq))
                .contentType(JSON)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.UNAUTHORIZED.name())));
    }

    @Test
    public void confirmAccount_success_anonymous() throws Exception {
        long clientId = generateId();
        String token = randomString();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    @Test
    public void confirmAccount_success_client() throws Exception {
        String jwt = createJwt(Role.USER);
        long clientId = generateId();
        String token = randomString();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    @Test
    public void confirmAccount_success_admin() throws Exception {
        String jwt = createJwt(Role.ADMIN);
        long clientId = generateId();
        String token = randomString();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    @Test
    public void sendToken_success_anonymous() throws Exception {
        long clientId = generateId();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SEND_TOKEN)
                .param("clientId", Long.toString(clientId)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    @Test
    public void sendToken_success_client() throws Exception {
        String jwt = createJwt(Role.USER);
        long clientId = generateId();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SEND_TOKEN)
                .param("clientId", Long.toString(clientId))
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    @Test
    public void sendToken_success_admin() throws Exception {
        String jwt = createJwt(Role.ADMIN);
        long clientId = generateId();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SEND_TOKEN)
                .param("clientId", Long.toString(clientId))
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    @Test
    public void resetPassword_success_anonymous() throws Exception {
        String mail = randomString();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.RESET_PASSWORD)
                .param("mail", mail))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())));
    }

    @Test
    public void resetPassword_success_client() throws Exception {
        String jwt = createJwt(Role.USER);
        String mail = randomString();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.RESET_PASSWORD)
                .param("mail", mail)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())));
    }

    @Test
    public void resetPassword_success_admin() throws Exception {
        String jwt = createJwt(Role.ADMIN);
        String mail = randomString();

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.RESET_PASSWORD)
                .param("mail", mail)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())));
    }

    private String createJwt(Role role) {
        String mail = randomString();
        ClientDetails client = new ClientDetails(
                new ClientDetails.Client(generateId(), mail, randomString(), role, true));
        when(userDetailsService.loadUserByUsername(any())).thenReturn(client);

        return jwtProvider.createToken(mail);
    }
}
