package rgo.cloud.authentication.db.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rgo.cloud.authentication.db.storage.repository.TxClientRepositoryDecorator;
import rgo.cloud.authentication.db.storage.repository.TxConfirmationTokenRepositoryDecorator;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.common.spring.storage.DbTxManager;

@Configuration
public class TxRepositoryConfig {

    @Bean
    public ConfirmationTokenRepository tokenRepository(ConfirmationTokenRepository repo, DbTxManager tx) {
        return new TxConfirmationTokenRepositoryDecorator(repo, tx);
    }

    @Bean
    public ClientRepository clientRepository(ClientRepository repo, DbTxManager tx) {
        return new TxClientRepositoryDecorator(repo, tx);
    }
}
