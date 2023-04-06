package rgo.cloud.authentication.boot.api.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.WebTest;
import rgo.cloud.security.config.util.Endpoint;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static rgo.cloud.common.spring.util.RequestUtil.JSON;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class MeRestControllerValidateTest extends WebTest {

    @BeforeEach
    public void setUp() {
        initMvc();
    }

    @Test
    public void findById_idIsNotPositive() throws Exception {
        long entityId = -1L;
        String errorMessage = "The entityId is not positive.";

        mvc.perform(get(Endpoint.Me.BASE_URL + "/" + entityId))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }

    @Test
    public void findByMail_mailIsEmpty() throws Exception {
        String mail = " ";
        String errorMessage = "The mail is empty.";

        mvc.perform(get(Endpoint.Me.BASE_URL + "?mail=" + mail))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.INVALID_RQ.name())))
                .andExpect(jsonPath("$.status.description", equalTo(errorMessage)));
    }
}
