package rgo.cloud.authentication.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.boot.storage.repository.ClientRepository;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.CommonTest;
import rgo.cloud.security.config.util.Endpoint;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.common.api.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class MeRestControllerTest extends CommonTest {

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

        mvc.perform(get(Endpoint.Me.BASE_URL + "/" + fakeId))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", nullValue()));
    }

    @Test
    public void findById_found() throws Exception {
        Client saved = repository.save(createRandomClient());

        mvc.perform(get(Endpoint.Me.BASE_URL + "/" + saved.getEntityId()))
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
}
