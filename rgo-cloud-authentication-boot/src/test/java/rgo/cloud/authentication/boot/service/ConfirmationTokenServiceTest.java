package rgo.cloud.authentication.boot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.boot.config.properties.TokenProperties;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.common.spring.test.CommonTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static rgo.cloud.authentication.boot.EntityGenerator.*;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;

@SpringBootTest
@ActiveProfiles("test")
public class ConfirmationTokenServiceTest extends CommonTest {

    @Autowired
    private ConfirmationTokenService service;

    @Autowired
    private TokenProperties config;

    @Autowired
    private ConfirmationTokenRepository tokenRepository;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        truncateTables();
    }

    @Test
    public void findByClientIdAndToken_empty() {
        long fakeClientId = generateId();

        Optional<ConfirmationToken> token = service.findByClientIdAndToken(fakeClientId);

        assertTrue(token.isEmpty());
    }

    @Test
    public void findByClientIdAndToken_present() {
        Client client = clientRepository.save(createRandomClient());
        ConfirmationToken saved = tokenRepository.save(createRandomFullConfirmationToken(client, config.getTokenLength()));

        Optional<ConfirmationToken> found = service.findByClientIdAndToken(client.getEntityId());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getToken(), found.get().getToken());
        assertEquals(saved.getClient().toString(), found.get().getClient().toString());
        assertEquals(saved.getExpiryDate(), found.get().getExpiryDate());
    }

    @Test
    public void save() {
        Client client = clientRepository.save(createRandomClient());
        ConfirmationToken created = createRandomConfirmationToken(client);

        ConfirmationToken saved = service.save(created);

        assertEquals(created.getClient().toString(), saved.getClient().toString());
    }

    @Test
    public void update() {
        Client client = clientRepository.save(createRandomClient());
        ConfirmationToken saved = tokenRepository.save(createRandomFullConfirmationToken(client, config.getTokenLength()));

        ConfirmationToken updated = service.update(createRandomFullConfirmationToken(client, config.getTokenLength()));

        assertNotEquals(updated.getToken(), saved.getToken());
        assertEquals(updated.getClient().toString(), saved.getClient().toString());
    }
}
