package rgo.cloud.authentication.db.storage.repository;

import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.util.List;
import java.util.Optional;

public class TxClientRepositoryDecorator implements ClientRepository {
    private final ClientRepository delegate;
    private final DbTxManager tx;

    public TxClientRepositoryDecorator(ClientRepository delegate, DbTxManager tx) {
        this.delegate = delegate;
        this.tx = tx;
    }

    @Override
    public List<Client> findAll() {
        return tx.tx(delegate::findAll);
    }

    @Override
    public Optional<Client> findById(Long entityId) {
        return tx.tx(() -> delegate.findById(entityId));
    }

    @Override
    public Optional<Client> findByMail(String mail) {
        return tx.tx(() -> delegate.findByMail(mail));
    }

    @Override
    public boolean exists(Long entityId) {
        return tx.tx(() -> delegate.exists(entityId));
    }

    @Override
    public Client save(Client client) {
        return tx.tx(() -> delegate.save(client));
    }

    @Override
    public Client update(Client client) {
        return tx.tx(() -> delegate.update(client));
    }

    @Override
    public Client updateStatus(Long entityId, boolean status) {
        return tx.tx(() -> delegate.updateStatus(entityId, status));
    }

    @Override
    public void resetPassword(String mail, String password) {
        tx.tx(() -> delegate.resetPassword(mail, password));
    }
}
