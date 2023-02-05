package rgo.cloud.authentication.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rgo.cloud.authentication.boot.storage.repository.natural.PostgresClientRepository;
import rgo.cloud.authentication.boot.storage.repository.natural.PostgresConfirmationTokenRepository;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.common.spring.storage.DbTxManager;

@Configuration
public class NativeRepositoryConfig {

    @Bean
    public ConfirmationTokenRepository nativeConfirmationTokenRepository(DbTxManager tx) {
        return new PostgresConfirmationTokenRepository(tx);
    }

    @Bean
    public ClientRepository nativeClientRepository(DbTxManager tx) {
        return new PostgresClientRepository(tx);
    }
}
