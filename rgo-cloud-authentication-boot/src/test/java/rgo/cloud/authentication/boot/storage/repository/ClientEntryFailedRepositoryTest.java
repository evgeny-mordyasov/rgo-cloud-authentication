package rgo.cloud.authentication.boot.storage.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.db.api.repository.ClientEntryFailedRepository;
import rgo.cloud.common.spring.test.CommonTest;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClientEntryFailed;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@ActiveProfiles("test")
public class ClientEntryFailedRepositoryTest extends CommonTest {

    @Autowired
    private ClientEntryFailedRepository clientEntryFailedRepository;

    @BeforeEach
    public void setUp() {
        truncateTables();
    }

    @Test
    public void findByMail_notFound() {
        String fakeMail = randomString();

        Optional<ClientEntryFailed> found = clientEntryFailedRepository.findByMail(fakeMail);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findByMail_found() {
        ClientEntryFailed saved = clientEntryFailedRepository.save(createRandomClientEntryFailed());

        Optional<ClientEntryFailed> found = clientEntryFailedRepository.findByMail(saved.getMail());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getMail(), found.get().getMail());
    }

    @Test
    public void save() {
        ClientEntryFailed created = createRandomClientEntryFailed();

        ClientEntryFailed saved = clientEntryFailedRepository.save(created);

        assertEquals(created.getMail(), saved.getMail());
    }

    @Test
    public void updateAttempts() {
        int attempts = ThreadLocalRandom.current().nextInt(1, 10);
        ClientEntryFailed saved = clientEntryFailedRepository.save(createRandomClientEntryFailed());

        clientEntryFailedRepository.updateAttempts(attempts, saved.getMail());

        Optional<ClientEntryFailed> found = clientEntryFailedRepository.findByMail(saved.getMail());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getMail(), found.get().getMail());
        assertEquals(attempts, found.get().getAttempts());
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

        clientEntryFailedRepository.resetAttempts(saved.getMail());

        Optional<ClientEntryFailed> opt = clientEntryFailedRepository.findByMail(saved.getMail());

        assertTrue(opt.isPresent());
        assertEquals(saved.getEntityId(), opt.get().getEntityId());
        assertEquals(saved.getMail(), opt.get().getMail());
        assertEquals(0, opt.get().getAttempts());
    }

    @Test
    public void block() {
        ClientEntryFailed saved = clientEntryFailedRepository.save(createRandomClientEntryFailed());

        clientEntryFailedRepository.block(saved.getMail());

        Optional<ClientEntryFailed> found = clientEntryFailedRepository.findByMail(saved.getMail());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getMail(), found.get().getMail());
        assertEquals(0, found.get().getAttempts());
    }
}
