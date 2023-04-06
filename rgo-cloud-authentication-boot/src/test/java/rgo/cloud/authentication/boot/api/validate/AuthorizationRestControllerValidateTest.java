package rgo.cloud.authentication.boot.api.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.WebTest;
import rgo.cloud.security.config.util.Endpoint;

import static org.hamcrest.Matchers.*;
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
public class AuthorizationRestControllerValidateTest extends WebTest {

    @BeforeEach
    public void setUp() {
        initMvc();
    }

    @Test
    public void signUp_surnameIsEmpty() throws Exception {
        String surname = " ";
        String errorMessage = "The surname is empty.";

        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(surname)
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void signUp_nameIsEmpty() throws Exception {
        String name = " ";
        String errorMessage = "The name is empty.";

        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(name)
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void signUp_patronymicIsEmpty() throws Exception {
        String patronymic = " ";
        String errorMessage = "The patronymic is empty.";
        
        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(patronymic)
                .mail(randomString())
                .password(randomString())
                .build();
        
        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void signUp_mailIsEmpty() throws Exception {
        String mail = " ";
        String errorMessage = "The mail is empty.";

        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(mail)
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void signUp_passwordIsEmpty() throws Exception {
        String password = " ";
        String errorMessage = "The password is empty.";

        AuthorizationSignUpRequest rq = AuthorizationSignUpRequest.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(password)
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_UP)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void signIn_mailIsEmpty() throws Exception {
        String mail = " ";
        String errorMessage = "The mail is empty.";

        AuthorizationSignInRequest rq = AuthorizationSignInRequest.builder()
                .mail(mail)
                .password(randomString())
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void signIn_passwordIsEmpty() throws Exception {
        String password = " ";
        String errorMessage = "The password is empty.";

        AuthorizationSignInRequest rq = AuthorizationSignInRequest.builder()
                .mail(randomString())
                .password(password)
                .build();

        mvc.perform(post(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SIGN_IN)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void confirmAccount_clientIdIsNotPositive() throws Exception {
        long clientId = -generateId();
        String token = randomString();
        String errorMessage = "The clientId is not positive.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void confirmAccount_tokenIsEmpty() throws Exception {
        long clientId = generateId();
        String token = " ";
        String errorMessage = "The token is empty.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.CONFIRM_ACCOUNT)
                .param("clientId", Long.toString(clientId))
                .param("token", token))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void sendToken_clientIdIsNotPositive() throws Exception {
        long clientId = -generateId();
        String errorMessage = "The clientId is not positive.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.SEND_TOKEN)
                .param("clientId", Long.toString(clientId)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }

    @Test
    public void resetPassword_mailIsEmpty() throws Exception {
        String mail = " ";
        String errorMessage = "The mail is empty.";

        mvc.perform(multipart(Endpoint.Authorization.BASE_URL + Endpoint.Authorization.RESET_PASSWORD)
                .param("mail", mail))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", is(errorMessage)));
    }
}
