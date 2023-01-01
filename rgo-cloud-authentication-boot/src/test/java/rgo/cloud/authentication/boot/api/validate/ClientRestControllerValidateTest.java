package rgo.cloud.authentication.boot.api.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientUpdateRequest;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.CommonTest;
import rgo.cloud.security.config.util.Endpoint;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.common.api.util.JsonUtil.toJson;
import static rgo.cloud.common.api.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class ClientRestControllerValidateTest extends CommonTest {

    @BeforeEach
    public void setUp() {
       initMvc();
    }

    @Test
    public void findById_idIsNotPositive() throws Exception {
        long entityId = -1L;
        String errorMessage = "The entityId is not positive.";

        mvc.perform(get(Endpoint.Client.BASE_URL + "/" + entityId))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void findByMail_mailIsEmpty() throws Exception {
        String mail = " ";
        String errorMessage = "The mail is empty.";

        mvc.perform(get(Endpoint.Client.BASE_URL + "?mail=" + mail))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void update_entityIdIsNull() throws Exception {
        final String errorMessage = "The entityId is null.";

        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(null)
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void update_entityIdIsNotPositive() throws Exception {
        final Long entityId = -generateId();
        final String errorMessage = "The entityId is not positive.";

        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(entityId)
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void update_surnameIsEmpty() throws Exception {
        String surname = " ";
        String errorMessage = "The surname is empty.";

        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(generateId())
                .surname(surname)
                .name(randomString())
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void update_nameIsEmpty() throws Exception {
        String name = " ";
        String errorMessage = "The name is empty.";

        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(generateId())
                .surname(randomString())
                .name(name)
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void update_patronymicIsEmpty() throws Exception {
        String patronymic = " ";
        String errorMessage = "The patronymic is empty.";

        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(generateId())
                .surname(randomString())
                .name(randomString())
                .patronymic(patronymic)
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void update_passwordIsEmpty() throws Exception {
        String password = " ";
        String errorMessage = "The password is empty.";

        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(generateId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .password(password)
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }
}
