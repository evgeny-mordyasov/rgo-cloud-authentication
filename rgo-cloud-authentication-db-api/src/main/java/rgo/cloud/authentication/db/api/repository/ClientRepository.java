package rgo.cloud.authentication.db.api.repository;

import rgo.cloud.authentication.db.api.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    List<Client> findAll();

    Optional<Client> findById(Long entityId);

    Optional<Client> findByMail(String mail);

    boolean exists(Long entityId);

    Client save(Client client);

    Client update(Client client);

    Client updateStatus(Long entityId, boolean status);

    void resetPassword(String mail, String password);
}
