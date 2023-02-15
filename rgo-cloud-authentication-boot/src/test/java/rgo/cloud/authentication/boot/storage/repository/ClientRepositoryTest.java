package rgo.cloud.authentication.boot.storage.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.common.api.model.Role;
import rgo.cloud.common.spring.test.CommonTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@ActiveProfiles("test")
public class ClientRepositoryTest extends CommonTest {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        truncateTables();
    }

    @Test
    public void findAll_noOneHasBeenFound() {
        int noOneHasBeenFound = 0;

        List<Client> found = clientRepository.findAll();

        assertEquals(noOneHasBeenFound, found.size());
    }

    @Test
    public void findAll_foundOne() {
        int foundOne = 1;
        clientRepository.save(createRandomClient());

        List<Client> found = clientRepository.findAll();

        assertEquals(foundOne, found.size());
    }

    @Test
    public void findAll_foundALot() {
        int foundALot = 2;
        clientRepository.save(createRandomClient());
        clientRepository.save(createRandomClient());

        List<Client> found = clientRepository.findAll();

        assertEquals(foundALot, found.size());
    }

    @Test
    public void findById_notFound() {
        long fakeId = generateId();

        Optional<Client> found = clientRepository.findById(fakeId);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findById_found() {
        Client saved = clientRepository.save(createRandomClient());

        Optional<Client> found = clientRepository.findById(saved.getEntityId());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getSurname(), found.get().getSurname());
        assertEquals(saved.getName(), found.get().getName());
        assertEquals(saved.getPatronymic(), found.get().getPatronymic());
        assertEquals(saved.getMail(), found.get().getMail());
        assertEquals(saved.getPassword(), found.get().getPassword());
        assertEquals(saved.getCreatedDate(), found.get().getCreatedDate());
        assertEquals(saved.getLastModifiedDate(), found.get().getLastModifiedDate());
        assertFalse(saved.isVerified());
        assertEquals(saved.getRole(), found.get().getRole());
    }

    @Test
    public void findByMail_notFound() {
        String fakeMail = randomString();

        Optional<Client> found = clientRepository.findByMail(fakeMail);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findByMail_found() {
        Client saved = clientRepository.save(createRandomClient());

        Optional<Client> found = clientRepository.findByMail(saved.getMail());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getSurname(), found.get().getSurname());
        assertEquals(saved.getName(), found.get().getName());
        assertEquals(saved.getPatronymic(), found.get().getPatronymic());
        assertEquals(saved.getMail(), found.get().getMail());
        assertEquals(saved.getPassword(), found.get().getPassword());
        assertEquals(saved.getCreatedDate(), found.get().getCreatedDate());
        assertEquals(saved.getLastModifiedDate(), found.get().getLastModifiedDate());
        assertFalse(saved.isVerified());
        assertEquals(saved.getRole(), found.get().getRole());
    }

    @Test
    public void save() {
        Client created = createRandomClient();

        Client saved = clientRepository.save(created);

        assertEquals(created.getSurname(), saved.getSurname());
        assertEquals(created.getName(), saved.getName());
        assertEquals(created.getPatronymic(), saved.getPatronymic());
        assertEquals(created.getMail(), saved.getMail());
        assertEquals(created.getPassword(), saved.getPassword());
        assertFalse(saved.isVerified());
        assertEquals(Role.USER, saved.getRole());
    }

    @Test
    public void update() {
        Client saved = clientRepository.save(createRandomClient());
        Client newObj = Client.builder()
                .entityId(saved.getEntityId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(saved.getMail())
                .password(randomString())
                .build();

        Client updated = clientRepository.update(newObj);

        assertEquals(newObj.getEntityId(), updated.getEntityId());
        assertEquals(newObj.getSurname(), updated.getSurname());
        assertEquals(newObj.getName(), updated.getName());
        assertEquals(newObj.getPatronymic(), updated.getPatronymic());
        assertEquals(newObj.getMail(), updated.getMail());
        assertEquals(newObj.getPassword(), updated.getPassword());
        assertEquals(saved.isVerified(), updated.isVerified());
        assertEquals(saved.getRole(), updated.getRole());
    }

    @Test
    public void updateStatus_activeIsTrue() {
        Client saved = clientRepository.save(createRandomClient());
        assertFalse(saved.isVerified());

        Client updated = clientRepository.updateStatus(saved.getEntityId(), true);

        assertEquals(updated.getEntityId(), updated.getEntityId());
        assertEquals(updated.getSurname(), updated.getSurname());
        assertEquals(updated.getName(), updated.getName());
        assertEquals(updated.getPatronymic(), updated.getPatronymic());
        assertEquals(updated.getMail(), updated.getMail());
        assertEquals(updated.getPassword(), updated.getPassword());
        assertTrue(updated.isVerified());
        assertEquals(saved.getRole(), updated.getRole());
    }

    @Test
    public void resetPassword() {
        String generatedPassword = randomString();
        Client saved = clientRepository.save(createRandomClient());

        clientRepository.resetPassword(saved.getMail(), generatedPassword);

        Optional<Client> opt = clientRepository.findById(saved.getEntityId());
        assertTrue(opt.isPresent());
        assertEquals(generatedPassword, opt.get().getPassword());
    }

    @Test
    public void resetPassword_exception() {
        String generatedPassword = randomString();
        Client saved = clientRepository.save(createRandomClient());
        clientRepository.save(createRandomClient().toBuilder().mail(saved.getMail()).build());

        assertThrows(RuntimeException.class, () -> clientRepository.resetPassword(saved.getMail(), generatedPassword), "Tx failed.");
    }
}
