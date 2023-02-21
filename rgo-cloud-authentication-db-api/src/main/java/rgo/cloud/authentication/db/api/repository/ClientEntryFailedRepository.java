package rgo.cloud.authentication.db.api.repository;

import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;

import java.util.Optional;

public interface ClientEntryFailedRepository {
    Optional<ClientEntryFailed> findByMail(String mail);

    ClientEntryFailed save(ClientEntryFailed cef);

    void updateAttempts(int attempts, String mail);

    void resetAttempts(String mail);

    void block(String mail);
}
