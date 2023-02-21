package rgo.cloud.authentication.boot.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import rgo.cloud.authentication.boot.api.decorator.AuthorizationFacadeDecorator;
import rgo.cloud.authentication.boot.api.decorator.ClientServiceDecorator;
import rgo.cloud.authentication.boot.facade.AuthorizationFacade;
import rgo.cloud.authentication.mail.config.MailConfig;
import rgo.cloud.authentication.service.ClientEntryFailedService;
import rgo.cloud.authentication.service.ClientService;
import rgo.cloud.authentication.service.ConfirmationTokenService;
import rgo.cloud.authentication.mail.MailSender;
import rgo.cloud.authentication.service.config.ServiceConfig;
import rgo.cloud.common.spring.config.AspectConfig;
import rgo.cloud.security.config.SecurityConfig;
import rgo.cloud.security.config.jwt.JwtProvider;

@Configuration
@ConfigurationPropertiesScan
@Import(value = { SecurityConfig.class,
        AspectConfig.class,
        ServiceConfig.class,
        MailConfig.class})
public class ApplicationConfig {

    @Bean
    public AuthorizationFacade authorizationFacade(
            ClientService clientService,
            ClientEntryFailedService clientEntryFailedService,
            ConfirmationTokenService tokenService,
            MailSender sender,
            JwtProvider jwtProvider,
            AuthenticationManager authenticationManager
    ) {
        return new AuthorizationFacade(
                clientService,
                clientEntryFailedService,
                tokenService,
                sender,
                jwtProvider,
                authenticationManager);
    }

    @Bean
    public AuthorizationFacadeDecorator authorizationDecorator(AuthorizationFacade facade) {
        return new AuthorizationFacadeDecorator(facade);
    }

    @Bean
    public ClientServiceDecorator clientDecorator(ClientService service) {
        return new ClientServiceDecorator(service);
    }
}
