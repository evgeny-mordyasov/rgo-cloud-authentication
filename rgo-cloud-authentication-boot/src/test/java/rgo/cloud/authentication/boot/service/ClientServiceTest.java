package rgo.cloud.authentication.boot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.boot.storage.repository.ClientRepository;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.common.api.exception.EntityNotFoundException;
import rgo.cloud.common.api.exception.ViolatesConstraintException;
import rgo.cloud.common.api.model.Role;
import rgo.cloud.common.spring.test.CommonTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.common.spring.util.TestCommonUtil.generateId;
import static rgo.cloud.common.spring.util.TestCommonUtil.randomString;

@SpringBootTest
@ActiveProfiles("test")
public class ClientServiceTest extends CommonTest {

    @Autowired
    private ClientService service;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ClientRepository repository;

    @BeforeEach
    public void setUp() {
        truncateTables();
    }

    @Test
    public void findById_notFound() {
        Long fakeId = generateId();

        Optional<Client> found = service.findById(fakeId);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findById_found() {
        Client created = createRandomClient();
        Client saved = repository.save(created);

        Optional<Client> found = service.findById(saved.getEntityId());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(created.getSurname(), found.get().getSurname());
        assertEquals(created.getName(), found.get().getName());
        assertEquals(created.getPatronymic(), found.get().getPatronymic());
        assertEquals(created.getMail(), found.get().getMail());
        assertEquals(created.getPassword(), found.get().getPassword());
    }

    @Test
    public void findByMail_notFound() {
        String fakeMail = randomString();

        Optional<Client> found = service.findByMail(fakeMail);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findByMail_found() {
        Client created = createRandomClient();
        repository.save(created);

        Optional<Client> found = service.findByMail(created.getMail());

        assertTrue(found.isPresent());
        assertEquals(created.getSurname(), found.get().getSurname());
        assertEquals(created.getName(), found.get().getName());
        assertEquals(created.getPatronymic(), found.get().getPatronymic());
        assertEquals(created.getMail(), found.get().getMail());
        assertEquals(created.getPassword(), found.get().getPassword());
    }

    @Test
    public void save() {
        Client created = createRandomClient();

        Client saved = service.save(created);

        assertEquals(created.getSurname(), saved.getSurname());
        assertEquals(created.getName(), saved.getName());
        assertEquals(created.getPatronymic(), saved.getPatronymic());
        assertEquals(created.getMail(), saved.getMail());
        assertTrue(encoder.matches(created.getPassword(), saved.getPassword()));
        assertFalse(saved.isActive());
        assertEquals(Role.USER, saved.getRole());
    }

    @Test
    public void save_mailAlreadyExists() {
        Client created = createRandomClient();
        repository.save(created);

        assertThrows(ViolatesConstraintException.class, () -> service.save(created), "Client by mail already exist.");
    }

    @Test
    public void update_passwordUpdated() {
        Client saved = repository.save(createRandomClient());
        Client newObj = Client.builder()
                .entityId(saved.getEntityId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(saved.getMail())
                .password(randomString())
                .build();

        Client updated = service.update(newObj);

        assertEquals(newObj.getEntityId(), updated.getEntityId());
        assertEquals(newObj.getSurname(), updated.getSurname());
        assertEquals(newObj.getName(), updated.getName());
        assertEquals(newObj.getPatronymic(), updated.getPatronymic());
        assertEquals(newObj.getMail(), updated.getMail());
        assertTrue(encoder.matches(newObj.getPassword(), updated.getPassword()));
        assertTrue(saved.getLastModifiedDate().getNano() < updated.getLastModifiedDate().getNano());
        assertEquals(saved.isActive(), updated.isActive());
        assertEquals(saved.getRole(), updated.getRole());
    }

    @Test
    public void update_notFound() {
        Client fakeClient = createRandomClient();

        assertThrows(EntityNotFoundException.class, () -> service.update(fakeClient), "The client by id not found.");
    }

    @Test
    public void updateStatus_statusUpdated() {
        Client saved = repository.save(createRandomClient());
        Client newObj = Client.builder()
                .entityId(saved.getEntityId())
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();

        Client updated = service.updateStatus(saved.getEntityId(), true);

        assertEquals(newObj.getEntityId(), updated.getEntityId());
        assertTrue(updated.isActive());
    }

    @Test
    public void updateStatus_notFound() {
        Long fakeId = generateId();

        assertThrows(EntityNotFoundException.class, () -> service.updateStatus(fakeId, true), "The client by id not found.");
    }
}
