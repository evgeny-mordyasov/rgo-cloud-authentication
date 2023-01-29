package rgo.cloud.authentication.boot.api.permit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientUpdateRequest;
import rgo.cloud.common.api.model.Role;
import rgo.cloud.common.api.rest.StatusCode;
import rgo.cloud.common.spring.test.CommonTest;
import rgo.cloud.security.config.domain.ClientDetails;
import rgo.cloud.security.config.jwt.JwtProvider;
import rgo.cloud.security.config.jwt.properties.JwtProperties;
import rgo.cloud.security.config.util.Endpoint;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
public class ClientRestControllerPermitTest extends CommonTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtProperties config;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup() throws Exception {
        truncateTables();
        initSecurityMvc();
    }

    @Test
    public void update_forbidden_anonymous() throws Exception {
        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(generateId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.FORBIDDEN.name())));
    }

    @Test
    public void update_success_client() throws Exception {
        String jwt = createJwt(Role.USER);
        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(generateId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    @Test
    public void update_success_admin() throws Exception {
        String jwt = createJwt(Role.ADMIN);
        ClientUpdateRequest rq = ClientUpdateRequest.builder()
                .entityId(generateId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .password(randomString())
                .build();

        mvc.perform(put(Endpoint.Client.BASE_URL)
                .content(toJson(rq))
                .contentType(JSON)
                .cookie(new Cookie(config.getAuthCookieName(), jwt)))
                .andExpect(content().contentType(JSON))
                .andExpect(jsonPath("$.status.code", is(StatusCode.ENTITY_NOT_FOUND.name())));
    }

    private String createJwt(Role role) {
        String mail = randomString();
        ClientDetails client = new ClientDetails(
                new ClientDetails.Client(generateId(), mail, randomString(), role, true));
        when(userDetailsService.loadUserByUsername(any())).thenReturn(client);

        return jwtProvider.createToken(mail);
    }
}
