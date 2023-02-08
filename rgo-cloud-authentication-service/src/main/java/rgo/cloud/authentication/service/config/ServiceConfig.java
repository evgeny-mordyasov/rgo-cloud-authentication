package rgo.cloud.authentication.service.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.authentication.db.config.PersistenceConfig;
import rgo.cloud.authentication.service.ClientService;
import rgo.cloud.authentication.service.ConfirmationTokenService;
import rgo.cloud.authentication.service.config.properties.TokenProperties;

@Configuration
@ConfigurationPropertiesScan
@Import(PersistenceConfig.class)
public class ServiceConfig {

    @Bean
    public ClientService clientService(ClientRepository clientRepository, PasswordEncoder encoder) {
        return new ClientService(clientRepository, encoder);
    }

    @Bean
    public ConfirmationTokenService tokenService(ConfirmationTokenRepository tokenRepository, TokenProperties config) {
        return new ConfirmationTokenService(tokenRepository, config);
    }
}
