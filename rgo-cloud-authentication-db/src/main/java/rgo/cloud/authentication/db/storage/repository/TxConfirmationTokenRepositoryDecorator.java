package rgo.cloud.authentication.db.storage.repository;

import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.util.Optional;

public class TxConfirmationTokenRepositoryDecorator implements ConfirmationTokenRepository {
    private final ConfirmationTokenRepository delegate;
    private final DbTxManager tx;


    public TxConfirmationTokenRepositoryDecorator(ConfirmationTokenRepository delegate, DbTxManager tx) {
        this.delegate = delegate;
        this.tx = tx;
    }

    @Override
    public Optional<ConfirmationToken> findByClientId(Long clientId) {
        return tx.tx(() -> delegate.findByClientId(clientId));
    }

    @Override
    public ConfirmationToken save(ConfirmationToken ct) {
        return tx.tx(() -> delegate.save(ct));
    }

    @Override
    public ConfirmationToken update(ConfirmationToken ct) {
        return tx.tx(() -> delegate.update(ct));
    }
}
