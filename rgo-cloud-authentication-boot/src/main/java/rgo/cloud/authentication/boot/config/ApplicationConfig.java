package rgo.cloud.authentication.boot.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rgo.cloud.authentication.boot.config.properties.TokenProperties;
import rgo.cloud.authentication.boot.service.ClientService;
import rgo.cloud.authentication.boot.service.ConfirmationTokenService;
import rgo.cloud.authentication.boot.storage.ClientRepository;
import rgo.cloud.authentication.boot.storage.ConfirmationTokenRepository;

@Configuration
@ConfigurationPropertiesScan
public class ApplicationConfig {

    @Bean
    public ClientService clientService(ClientRepository repository) {
        return new ClientService(repository);
    }

    @Bean
    public ConfirmationTokenService tokenService(ConfirmationTokenRepository repository, TokenProperties config) {
        return new ConfirmationTokenService(repository, config);
    }
}
