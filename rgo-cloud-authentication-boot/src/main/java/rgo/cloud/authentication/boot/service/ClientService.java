package rgo.cloud.authentication.boot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.common.api.exception.EntityNotFoundException;
import rgo.cloud.common.api.exception.ViolatesConstraintException;

import java.util.Optional;

@Slf4j
public class ClientService {
    private final ClientRepository repository;
    private final PasswordEncoder encoder;

    public ClientService(ClientRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public Optional<Client> findById(Long entityId) {
        return repository.findById(entityId);
    }

    public Optional<Client> findByMail(String mail) {
        return repository.findByMail(mail);
    }

    public Client save(Client client) {
        checkMailForDuplicate(client.getMail());

        Client encodedClient = encodePassword(client);
        return repository.save(encodedClient);
    }

    private void checkMailForDuplicate(String mail) {
        findByMail(mail).ifPresent(ignored -> {
            String errorMsg = "Client by mail already exist.";
            log.error(errorMsg);
            throw new ViolatesConstraintException(errorMsg);
        });
    }

    private Client encodePassword(Client client) {
        return client.toBuilder()
                .password(encoder.encode(client.getPassword()))
                .build();
    }

    public Client update(Client client) {
        validateClient(client.getEntityId());

        Client encodedClient = encodePassword(client);
        return repository.update(encodedClient);
    }

    public Client updateStatus(Long entityId, boolean status) {
        validateClient(entityId);
        return repository.updateStatus(entityId, status);
    }

    private void validateClient(Long entityId) {
        if (!repository.exists(entityId)) {
            throw new EntityNotFoundException("The client by id not found.");
        }
    }

    public void resetPassword(String mail, String password) {
        String encodedPassword = encoder.encode(password);
        repository.resetPassword(mail, encodedPassword);
    }
}
