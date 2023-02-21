package rgo.cloud.authentication.db.storage.repository;

import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.db.api.repository.ClientEntryFailedRepository;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.util.Optional;

public class TxClientEntryFailedRepositoryDecorator implements ClientEntryFailedRepository {
    private final ClientEntryFailedRepository delegate;
    private final DbTxManager tx;

    public TxClientEntryFailedRepositoryDecorator(ClientEntryFailedRepository delegate, DbTxManager tx) {
        this.delegate = delegate;
        this.tx = tx;
    }

    @Override
    public Optional<ClientEntryFailed> findByMail(String mail) {
        return tx.tx(() -> delegate.findByMail(mail));
    }

    @Override
    public ClientEntryFailed save(ClientEntryFailed cef) {
        return tx.tx(() -> delegate.save(cef));
    }

    @Override
    public void updateAttempts(int attempts, String mail) {
        tx.tx(() -> delegate.updateAttempts(attempts, mail));
    }

    @Override
    public void resetAttempts(String mail) {
        tx.tx(() -> delegate.resetAttempts(mail));
    }

    @Override
    public void block(String mail) {
        tx.tx(() -> delegate.block(mail));
    }
}
