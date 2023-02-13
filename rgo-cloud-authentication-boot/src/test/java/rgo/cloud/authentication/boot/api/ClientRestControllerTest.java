package rgo.cloud.authentication.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.rest.api.client.request.ClientUpdateRequest;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.CommonTest;
import rgo.cloud.security.config.util.Endpoint;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.common.api.util.JsonUtil.toJson;
import static rgo.cloud.common.spring.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class ClientRestControllerTest extends CommonTest {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        truncateTables();
        initMvc();
    }

    @Test
    public void update() throws Exception {
        Client saved = clientRepository.save(createRandomClient());

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
                .andExpect(jsonPath("$.object.role", is(saved.getRole().name())));

        Optional<Client> opt = clientRepository.findById(saved.getEntityId());
        assertTrue(opt.isPresent());
        assertEquals(saved.getCreatedDate(), opt.get().getCreatedDate());
        assertNotEquals(saved.getLastModifiedDate(), opt.get().getLastModifiedDate());
    }

    @Test
    public void update_classificationDoesNotExistByCurrentId() throws Exception {
        long currentId = generateId();

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
