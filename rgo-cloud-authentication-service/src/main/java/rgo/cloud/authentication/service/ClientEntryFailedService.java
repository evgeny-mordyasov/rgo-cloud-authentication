package rgo.cloud.authentication.service;

import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.db.api.repository.ClientEntryFailedRepository;
import rgo.cloud.authentication.service.config.properties.ClientEntryFailedProperties;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ClientEntryFailedService {
    private final ClientEntryFailedRepository repository;
    private final ClientEntryFailedProperties config;

    public ClientEntryFailedService(ClientEntryFailedRepository repository, ClientEntryFailedProperties config) {
        this.repository = repository;
        this.config = config;
    }

    public ClientEntryFailed save(ClientEntryFailed cef) {
        return repository.save(cef);
    }

    public void updateAttempts(String mail) {
        Optional<ClientEntryFailed> opt = repository.findByMail(mail);

        if (opt.isEmpty()) {
            return;
        }

        int attempts = opt.get().getAttempts() + 1;
        if (attempts > config.getMaxFailedAttempts()) {
            repository.block(mail);
        } else {
            repository.updateAttempts(attempts, mail);
        }
    }

    public void resetAttempts(String mail) {
        repository.resetAttempts(mail);
    }

    public boolean isBlocked(String mail) {
        Optional<ClientEntryFailed> opt = repository.findByMail(mail);

        if (opt.isEmpty()) {
            return false;
        }

        LocalDateTime blocked = opt.get().getBlockDate().plus(config.getBlockTimeMs(), ChronoUnit.MILLIS);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return now.isBefore(blocked);
    }
}
