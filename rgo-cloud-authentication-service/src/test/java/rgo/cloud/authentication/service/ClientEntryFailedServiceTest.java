package rgo.cloud.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.db.api.repository.ClientEntryFailedRepository;
import rgo.cloud.authentication.service.config.ServiceConfig;
import rgo.cloud.authentication.service.config.TestServiceConfig;
import rgo.cloud.authentication.service.config.properties.ClientEntryFailedProperties;
import rgo.cloud.common.spring.test.PersistenceTest;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static rgo.cloud.authentication.db.utils.EntityGenerator.createRandomClientEntryFailed;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@ActiveProfiles("test")
@Import(TestServiceConfig.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ServiceConfig.class)
@TestPropertySource(value = "classpath:application-test.properties")
public class ClientEntryFailedServiceTest extends PersistenceTest {

    @Autowired
    private ClientEntryFailedService service;

    @Autowired
    private ClientEntryFailedRepository clientEntryFailedRepository;

    @Autowired
    private ClientEntryFailedProperties config;

    @BeforeEach
    public void setUp() {
        truncateTables();
    }

    @Test
    public void save() {
        ClientEntryFailed created = createRandomClientEntryFailed();

        ClientEntryFailed saved = service.save(created);

        assertEquals(created.getMail(), saved.getMail());
    }

    @Test
    public void updateAttempts_blockClient() {
        ClientEntryFailed saved = clientEntryFailedRepository.save(createRandomClientEntryFailed());

        clientEntryFailedRepository.updateAttempts(config.getMaxFailedAttempts(), saved.getMail());
        service.updateAttempts(saved.getMail());

        boolean isBlocked = service.isBlocked(saved.getMail());

        assertTrue(isBlocked);
    }

    @Test
    public void updateAttempts() {
        int attempts = ThreadLocalRandom.current().nextInt(2, config.getMaxFailedAttempts());
        ClientEntryFailed saved = clientEntryFailedRepository.save(createRandomClientEntryFailed());

        for (int i = 0; i < attempts; i++) {
            service.updateAttempts(saved.getMail());
        }

        Optional<ClientEntryFailed> opt = clientEntryFailedRepository.findByMail(saved.getMail());
        boolean isBlocked = service.isBlocked(saved.getMail());

        assertFalse(isBlocked);
        assertTrue(opt.isPresent());
        assertEquals(attempts, opt.get().getAttempts());
    }

    @Test
    public void resetAttempts() {
        int attempts = ThreadLocalRandom.current().nextInt(1, 10);
        ClientEntryFailed saved = clientEntryFailedRepository.save(createRandomClientEntryFailed());

        clientEntryFailedRepository.updateAttempts(attempts, saved.getMail());

        Optional<ClientEntryFailed> found = clientEntryFailedRepository.findByMail(saved.getMail());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getMail(), found.get().getMail());
        assertEquals(attempts, found.get().getAttempts());

        service.resetAttempts(saved.getMail());

        Optional<ClientEntryFailed> opt = clientEntryFailedRepository.findByMail(saved.getMail());

        assertTrue(opt.isPresent());
        assertEquals(saved.getEntityId(), opt.get().getEntityId());
        assertEquals(saved.getMail(), opt.get().getMail());
        assertEquals(0, opt.get().getAttempts());
    }

    @Test
    public void isBlocked_notFound() {
        String fakeMail = randomString();

        boolean isBlocked = service.isBlocked(fakeMail);

        assertFalse(isBlocked);
    }

    @Test
    public void isBlocked_found_clientIsNotBlocked() {
        ClientEntryFailed saved = clientEntryFailedRepository.save(createRandomClientEntryFailed());

        boolean isBlocked = service.isBlocked(saved.getMail());

        assertFalse(isBlocked);
    }
}