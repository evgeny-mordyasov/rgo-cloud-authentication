package rgo.cloud.authentication.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.boot.storage.repository.ClientRepository;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientUpdateRequest;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.CommonTest;
import rgo.cloud.security.config.util.Endpoint;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class ClientRestControllerTest extends CommonTest {

    @Autowired
    private ClientRepository repository;

    @BeforeEach
    public void setUp() {
        truncateTables();
        initMvc();
    }

    @Test
    public void findById_notFound() throws Exception {
        Long fakeId = generateId();

        mvc.perform(get(Endpoint.Client.BASE_URL + "/" + fakeId))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", nullValue()));
    }

    @Test
    public void findById_found() throws Exception {
        Client saved = repository.save(createRandomClient());

        mvc.perform(get(Endpoint.Client.BASE_URL + "/" + saved.getEntityId()))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", notNullValue()))
                .andExpect(jsonPath("$.object.entityId", is(saved.getEntityId().intValue())))
                .andExpect(jsonPath("$.object.surname", is(saved.getSurname())))
                .andExpect(jsonPath("$.object.name", is(saved.getName())))
                .andExpect(jsonPath("$.object.patronymic", is(saved.getPatronymic())))
                .andExpect(jsonPath("$.object.mail", is(saved.getMail())))
                .andExpect(jsonPath("$.object.role", is(saved.getRole().name())));
    }

    @Test
    public void findByMail_notFound() throws Exception {
        String fakeMail = randomString();

        mvc.perform(get(Endpoint.Client.BASE_URL + "?mail=" + fakeMail))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", nullValue()));
    }

    @Test
    public void findByMail_found() throws Exception {
        Client saved = repository.save(createRandomClient());

        mvc.perform(get(Endpoint.Client.BASE_URL + "?mail=" + saved.getMail()))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", notNullValue()))
                .andExpect(jsonPath("$.object.entityId", is(saved.getEntityId().intValue())))
                .andExpect(jsonPath("$.object.surname", is(saved.getSurname())))
                .andExpect(jsonPath("$.object.name", is(saved.getName())))
                .andExpect(jsonPath("$.object.patronymic", is(saved.getPatronymic())))
                .andExpect(jsonPath("$.object.mail", is(saved.getMail())))
                .andExpect(jsonPath("$.object.role", is(saved.getRole().name())));
    }

    @Test
    public void update() throws Exception {
        Client saved = repository.save(createRandomClient());

        Client updatedClient = createRandomClient();
        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(saved.getEntityId())
                .surname(updatedClient.getSurname())
                .name(updatedClient.getName())
                .patronymic(updatedClient.getPatronymic())
                .password(updatedClient.getPassword())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", notNullValue()))
                .andExpect(jsonPath("$.object.entityId", is(saved.getEntityId().intValue())))
                .andExpect(jsonPath("$.object.surname", is(updatedClient.getSurname())))
                .andExpect(jsonPath("$.object.name", is(updatedClient.getName())))
                .andExpect(jsonPath("$.object.patronymic", is(updatedClient.getPatronymic())))
                .andExpect(jsonPath("$.object.mail", is(saved.getMail())))
                .andExpect(jsonPath("$.object.role", is(saved.getRole().name())))
                .andExpect(jsonPath("$.object.createdDate", is(saved.getCreatedDate().toString())))
                .andExpect(jsonPath("$.object.lastModifiedDate", not(saved.getLastModifiedDate().toString())));
    }

    @Test
    public void update_classificationDoesNotExistByCurrentId() throws Exception {
        Long currentId = generateId();

        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(currentId)
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())))
                .andExpect(jsonPath("$.status.description", is("The client by id not found.")));
    }
}
