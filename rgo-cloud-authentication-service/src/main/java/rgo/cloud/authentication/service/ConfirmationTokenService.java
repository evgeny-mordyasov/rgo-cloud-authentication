package rgo.cloud.authentication.service;

import org.apache.commons.lang3.RandomStringUtils;
import rgo.cloud.authentication.service.config.properties.TokenProperties;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class ConfirmationTokenService {
    private final ConfirmationTokenRepository repository;
    private final TokenProperties config;

    public ConfirmationTokenService(ConfirmationTokenRepository repository, TokenProperties config) {
        this.repository = repository;
        this.config = config;
    }

    public Optional<ConfirmationToken> findByClientId(Long clientId) {
        return repository.findByClientId(clientId);
    }

    public ConfirmationToken save(ConfirmationToken ct) {
        ConfirmationToken token = generate(ct);
        return repository.save(token);
    }

    public ConfirmationToken update(ConfirmationToken ct) {
        ConfirmationToken token = generate(ct);
        return repository.update(token);
    }

    private ConfirmationToken generate(ConfirmationToken ct) {
        return ct.toBuilder()
                .token(generateToken())
                .expiryDate(expiryDateToken())
                .build();
    }

    private String generateToken() {
        return RandomStringUtils.randomNumeric(config.getTokenLength());
    }

    private LocalDateTime expiryDateToken() {
        return LocalDateTime.now()
                .plusHours(config.getLifetimeHours());
    }
}
