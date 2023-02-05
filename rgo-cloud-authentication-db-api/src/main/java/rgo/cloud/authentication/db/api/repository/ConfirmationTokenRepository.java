package rgo.cloud.authentication.db.api.repository;

import rgo.cloud.authentication.db.api.entity.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenRepository {
    Optional<ConfirmationToken> findByClientId(Long clientId);

    ConfirmationToken save(ConfirmationToken ct);

    ConfirmationToken update(ConfirmationToken ct);
}
