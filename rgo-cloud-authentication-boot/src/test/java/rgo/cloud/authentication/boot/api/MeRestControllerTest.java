package rgo.cloud.authentication.boot.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.CommonTest;
import rgo.cloud.security.config.util.Endpoint;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.common.spring.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class MeRestControllerTest extends CommonTest {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        truncateTables();
        initMvc();
    }

    @Test
    public void findByClientId_notFound() throws Exception {
        long fakeClientId = generateId();

        mvc.perform(get(Endpoint.Me.BASE_URL + "/" + fakeClientId))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", nullValue()));
    }

    @Test
    public void findByClientId_found() throws Exception {
        Client saved = clientRepository.save(createRandomClient());

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

    @Test
    public void findByMail_notFound() throws Exception {
        String fakeMail = randomString();

        mvc.perform(get(Endpoint.Me.BASE_URL + "?mail=" + fakeMail))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.SUCCESS.name())))
                .andExpect(jsonPath("$.status.description", nullValue()))
                .andExpect(jsonPath("$.object", nullValue()));
    }

    @Test
    public void findByMail_found() throws Exception {
        Client saved = clientRepository.save(createRandomClient());

        mvc.perform(get(Endpoint.Me.BASE_URL + "?mail=" + saved.getMail()))
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
