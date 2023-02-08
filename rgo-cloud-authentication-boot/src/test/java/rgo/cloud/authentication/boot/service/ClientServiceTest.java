package rgo.cloud.authentication.boot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.service.ClientService;
import rgo.cloud.common.api.exception.EntityNotFoundException;
import rgo.cloud.common.api.exception.ViolatesConstraintException;
import rgo.cloud.common.api.model.Role;
import rgo.cloud.common.spring.test.CommonTest;

import java.time.temporal.ChronoUnit;
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
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        truncateTables();
    }

    @Test
    public void findById_notFound() {
        long fakeId = generateId();

        Optional<Client> found = service.findById(fakeId);

        assertTrue(found.isEmpty());
    }

    @Test
    public void findById_found() {
        Client created = createRandomClient();
        Client saved = clientRepository.save(created);

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
        clientRepository.save(created);

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
        clientRepository.save(created);

        assertThrows(ViolatesConstraintException.class, () -> service.save(created), "Client by mail already exist.");
    }

    @Test
    public void update_passwordUpdated() {
        Client saved = clientRepository.save(createRandomClient());
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
        assertTrue(saved.getLastModifiedDate().isBefore(updated.getLastModifiedDate().plus(7, ChronoUnit.HOURS)));
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
        Client saved = clientRepository.save(createRandomClient());
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
        long fakeId = generateId();

        assertThrows(EntityNotFoundException.class, () -> service.updateStatus(fakeId, true), "The client by id not found.");
    }

    @Test
    public void resetPassword() {
        String generatedPassword = randomString();
        Client saved = clientRepository.save(createRandomClient());

        service.resetPassword(saved.getMail(), generatedPassword);

        Optional<Client> opt = clientRepository.findById(saved.getEntityId());
        assertTrue(opt.isPresent());
        assertTrue(encoder.matches(generatedPassword, opt.get().getPassword()));
    }
}
