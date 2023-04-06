package rgo.cloud.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.authentication.service.config.ServiceConfig;
import rgo.cloud.authentication.service.config.TestServiceConfig;
import rgo.cloud.common.spring.test.PersistenceTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static rgo.cloud.authentication.db.utils.EntityGenerator.*;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;

@ActiveProfiles("test")
@Import(TestServiceConfig.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceConfig.class)
public class ConfirmationTokenServiceTest extends PersistenceTest {

    @Autowired
    private ConfirmationTokenService service;

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

        Optional<ConfirmationToken> token = service.findByClientId(fakeClientId);

        assertTrue(token.isEmpty());
    }

    @Test
    public void findByClientIdAndToken_present() {
        Client client = clientRepository.save(createRandomClient());
        ConfirmationToken saved = tokenRepository.save(createRandomFullConfirmationToken(client));

        Optional<ConfirmationToken> found = service.findByClientId(client.getEntityId());

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
        ConfirmationToken saved = tokenRepository.save(createRandomFullConfirmationToken(client));

        ConfirmationToken updated = service.update(createRandomFullConfirmationToken(client));

        assertNotEquals(updated.getToken(), saved.getToken());
        assertEquals(updated.getClient().toString(), saved.getClient().toString());
    }
}
