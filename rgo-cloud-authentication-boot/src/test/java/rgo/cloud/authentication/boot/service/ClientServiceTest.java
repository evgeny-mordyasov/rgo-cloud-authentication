package rgo.cloud.authentication.boot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.boot.storage.ClientRepository;
import rgo.cloud.authentication.internal.api.exception.EntityNotFoundException;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.authentication.internal.api.storage.Role;

import javax.sql.DataSource;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomClient;
import static rgo.cloud.authentication.boot.TestCommonUtil.*;

@SpringBootTest
@ActiveProfiles("test")
public class ClientServiceTest {

    @Autowired
    private ClientService service;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private ClientRepository repository;

    @Autowired
    private DataSource h2;

    @BeforeEach
    public void setUp() {
        runScript("h2/truncate.sql", h2);
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
