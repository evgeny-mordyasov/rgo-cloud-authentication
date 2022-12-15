package rgo.cloud.authentication.boot.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import rgo.cloud.authentication.boot.storage.ClientRepository;
import rgo.cloud.authentication.internal.api.exception.EntityNotFoundException;
import rgo.cloud.authentication.internal.api.storage.Client;

import java.util.Optional;

public class ClientService {
    private final ClientRepository repository;
    private final PasswordEncoder encoder;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public Optional<Client> findByMail(String mail) {
        return repository.findByMail(mail);
    }

    public Client save(Client client) {
        Client encodedClient = encodePassword(client);
        return repository.save(encodedClient);
    }

    private Client encodePassword(Client client) {
        return client.toBuilder()
                .password(encoder.encode(client.getPassword()))
                .build();
    }

    public Client update(Client client) {
        validateClient(client.getEntityId());
        return repository.update(encodePassword(client));
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
}
