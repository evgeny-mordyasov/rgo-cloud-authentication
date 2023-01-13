package rgo.cloud.authentication.boot.storage.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.internal.api.storage.Client;
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
    private ClientRepository repository;

    @BeforeEach
    public void setUp() {
        truncateTables();
    }

    @Test
    public void findAll_noOneHasBeenFound() {
        int noOneHasBeenFound = 0;

        List<Client> found = repository.findAll();

        assertEquals(noOneHasBeenFound, found.size());
    }

    @Test
    public void findAll_foundOne() {
        int foundOne = 1;
        repository.save(createRandomClient());

        List<Client> found = repository.findAll();

        assertEquals(foundOne, found.size());
    }

    @Test
    public void findAll_foundALot() {
        int foundALot = 2;
        repository.save(createRandomClient());
        repository.save(createRandomClient());

        List<Client> found = repository.findAll();

        assertEquals(foundALot, found.size());
    }

    @Test
    public void findById_notFound() {
        long fakeId = generateId();

        Optional<Client> found = repository.findById(fakeId);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findById_found() {
        Client saved = repository.save(createRandomClient());

        Optional<Client> found = repository.findById(saved.getEntityId());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getSurname(), found.get().getSurname());
        assertEquals(saved.getName(), found.get().getName());
        assertEquals(saved.getPatronymic(), found.get().getPatronymic());
        assertEquals(saved.getMail(), found.get().getMail());
        assertEquals(saved.getPassword(), found.get().getPassword());
        assertEquals(saved.getCreatedDate(), found.get().getCreatedDate());
        assertEquals(saved.getLastModifiedDate(), found.get().getLastModifiedDate());
        assertFalse(saved.isActive());
        assertEquals(saved.getRole(), found.get().getRole());
    }

    @Test
    public void findByMail_notFound() {
        String fakeMail = randomString();

        Optional<Client> found = repository.findByMail(fakeMail);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findByMail_found() {
        Client saved = repository.save(createRandomClient());

        Optional<Client> found = repository.findByMail(saved.getMail());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getSurname(), found.get().getSurname());
        assertEquals(saved.getName(), found.get().getName());
        assertEquals(saved.getPatronymic(), found.get().getPatronymic());
        assertEquals(saved.getMail(), found.get().getMail());
        assertEquals(saved.getPassword(), found.get().getPassword());
        assertEquals(saved.getCreatedDate(), found.get().getCreatedDate());
        assertEquals(saved.getLastModifiedDate(), found.get().getLastModifiedDate());
        assertFalse(saved.isActive());
        assertEquals(saved.getRole(), found.get().getRole());
    }

    @Test
    public void save() {
        Client created = createRandomClient();

        Client saved = repository.save(created);

        assertEquals(created.getSurname(), saved.getSurname());
        assertEquals(created.getName(), saved.getName());
        assertEquals(created.getPatronymic(), saved.getPatronymic());
        assertEquals(created.getMail(), saved.getMail());
        assertEquals(created.getPassword(), saved.getPassword());
        assertFalse(saved.isActive());
        assertEquals(Role.USER, saved.getRole());
    }

    @Test
    public void update() {
        Client saved = repository.save(createRandomClient());
        Client newObj = Client.builder()
                .entityId(saved.getEntityId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(saved.getMail())
                .password(randomString())
                .build();

        Client updated = repository.update(newObj);

        assertEquals(newObj.getEntityId(), updated.getEntityId());
        assertEquals(newObj.getSurname(), updated.getSurname());
        assertEquals(newObj.getName(), updated.getName());
        assertEquals(newObj.getPatronymic(), updated.getPatronymic());
        assertEquals(newObj.getMail(), updated.getMail());
        assertEquals(newObj.getPassword(), updated.getPassword());
        assertEquals(saved.isActive(), updated.isActive());
        assertEquals(saved.getRole(), updated.getRole());
    }

    @Test
    public void updateStatus_activeIsTrue() {
        Client saved = repository.save(createRandomClient());
        assertFalse(saved.isActive());

        Client updated = repository.updateStatus(saved.getEntityId(), true);

        assertEquals(updated.getEntityId(), updated.getEntityId());
        assertEquals(updated.getSurname(), updated.getSurname());
        assertEquals(updated.getName(), updated.getName());
        assertEquals(updated.getPatronymic(), updated.getPatronymic());
        assertEquals(updated.getMail(), updated.getMail());
        assertEquals(updated.getPassword(), updated.getPassword());
        assertTrue(updated.isActive());
        assertEquals(saved.getRole(), updated.getRole());
    }

    @Test
    public void resetPassword() {
        String generatedPassword = randomString();
        Client saved = repository.save(createRandomClient());

        repository.resetPassword(saved.getMail(), generatedPassword);

        Optional<Client> opt = repository.findById(saved.getEntityId());
        assertTrue(opt.isPresent());
        assertEquals(generatedPassword, opt.get().getPassword());
    }

    @Test
    public void resetPassword_exception() {
        String generatedPassword = randomString();
        Client saved = repository.save(createRandomClient());
        repository.save(createRandomClient().toBuilder().mail(saved.getMail()).build());

        assertThrows(RuntimeException.class, () -> repository.resetPassword(saved.getMail(), generatedPassword), "Tx failed.");
    }
}
