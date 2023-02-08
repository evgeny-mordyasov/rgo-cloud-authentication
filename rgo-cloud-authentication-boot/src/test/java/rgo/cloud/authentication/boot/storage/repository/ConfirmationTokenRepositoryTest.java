package rgo.cloud.authentication.boot.storage.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.service.config.properties.TokenProperties;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.common.spring.test.CommonTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomFullConfirmationToken;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;

@SpringBootTest
@ActiveProfiles("test")
public class ConfirmationTokenRepositoryTest extends CommonTest {

    @Autowired
    private ConfirmationTokenRepository tokenRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TokenProperties config;

    @BeforeEach
    public void setUp() {
       truncateTables();
    }

    @Test
    public void findByClientIdAndToken_empty() {
        long fakeClientId = generateId();

        Optional<ConfirmationToken> token = tokenRepository.findByClientId(fakeClientId);

        assertTrue(token.isEmpty());
    }

    @Test
    public void findByClientIdAndToken_present() {
        Client client = clientRepository.save(createRandomClient());
        ConfirmationToken saved = tokenRepository.save(createRandomFullConfirmationToken(client, config.getTokenLength()));

        Optional<ConfirmationToken> found = tokenRepository.findByClientId(client.getEntityId());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getToken(), found.get().getToken());
        assertEquals(saved.getClient().toString(), found.get().getClient().toString());
        assertEquals(saved.getExpiryDate(), found.get().getExpiryDate());
    }

    @Test
    public void save() {
        Client client = clientRepository.save(createRandomClient());
        ConfirmationToken created = createRandomFullConfirmationToken(client, config.getTokenLength());

        ConfirmationToken saved = tokenRepository.save(created);

        assertEquals(created.getToken(), saved.getToken());
        assertEquals(created.getClient().toString(), saved.getClient().toString());
    }

    @Test
    public void update() {
        Client client = clientRepository.save(createRandomClient());
        ConfirmationToken saved = tokenRepository.save(createRandomFullConfirmationToken(client, config.getTokenLength()));

        ConfirmationToken updated = tokenRepository.update(createRandomFullConfirmationToken(client, config.getTokenLength()));

        assertNotEquals(updated.getToken(), saved.getToken());
        assertEquals(updated.getClient().toString(), saved.getClient().toString());
    }
}
