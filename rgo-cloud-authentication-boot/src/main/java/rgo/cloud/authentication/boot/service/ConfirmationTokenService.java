package rgo.cloud.authentication.boot.service;

import rgo.cloud.authentication.boot.config.properties.TokenProperties;
import rgo.cloud.authentication.boot.storage.ConfirmationTokenRepository;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public class ConfirmationTokenService {
    private final ConfirmationTokenRepository repository;
    private final TokenProperties config;

    public ConfirmationTokenService(ConfirmationTokenRepository repository, TokenProperties config) {
        this.repository = repository;
        this.config = config;
    }

    public Optional<ConfirmationToken> findByClientIdAndToken(Long clientId, String token) {
        return repository.findByClientIdAndToken(clientId, token);
    }

    public ConfirmationToken save(ConfirmationToken ct) {
        ConfirmationToken token = addExpiryDate(ct);
        return repository.save(token);
    }

    private ConfirmationToken addExpiryDate(ConfirmationToken ct) {
        return ct.toBuilder()
                .expiryDate(expiryDateToken())
                .build();
    }

    private LocalDateTime expiryDateToken() {
        return LocalDateTime.now()
                .plusHours(config.getLifetimeHours());
    }
}
