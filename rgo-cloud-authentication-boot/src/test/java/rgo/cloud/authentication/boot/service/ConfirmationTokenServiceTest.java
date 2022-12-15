package rgo.cloud.authentication.boot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rgo.cloud.authentication.boot.storage.ConfirmationTokenRepository;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

import javax.sql.DataSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rgo.cloud.authentication.boot.EntityGenerator.createRandomConfirmationToken;
import static rgo.cloud.authentication.boot.TestCommonUtil.*;

@SpringBootTest
@ActiveProfiles("test")
public class ConfirmationTokenServiceTest {

    @Autowired
    private ConfirmationTokenService service;

    @Autowired
    private ConfirmationTokenRepository repository;

    @Autowired
    private DataSource h2;

    @BeforeEach
    public void setUp() {
        runScript("h2/truncate.sql", h2);
    }

    @Test
    public void findByClientIdAndToken_empty() {
        long fakeClientId = generateId();
        String fakeToken = randomString();

        Optional<ConfirmationToken> token = service.findByClientIdAndToken(fakeClientId, fakeToken);

        assertTrue(token.isEmpty());
    }

    @Test
    public void findByClientIdAndToken_present() {
        ConfirmationToken saved = repository.save(createRandomConfirmationToken());

        Optional<ConfirmationToken> found = service.findByClientIdAndToken(saved.getClientId(), saved.getToken());

        assertTrue(found.isPresent());
        assertEquals(saved.getEntityId(), found.get().getEntityId());
        assertEquals(saved.getToken(), found.get().getToken());
        assertEquals(saved.getClientId(), found.get().getClientId());
        assertEquals(saved.getExpiryDate(), found.get().getExpiryDate());
    }

    @Test
    public void save() {
        ConfirmationToken created = createRandomConfirmationToken();

        ConfirmationToken saved = service.save(created);

        assertEquals(created.getToken(), saved.getToken());
        assertEquals(created.getClientId(), saved.getClientId());
    }
}
