package rgo.cloud.authentication.boot.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import rgo.cloud.authentication.boot.service.ClientService;
import rgo.cloud.authentication.boot.service.ConfirmationTokenService;
import rgo.cloud.authentication.boot.service.sender.MailSender;
import rgo.cloud.authentication.internal.api.rest.authorization.AuthorizedClient;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;
import rgo.cloud.common.api.exception.BannedException;
import rgo.cloud.common.api.exception.EntityNotFoundException;
import rgo.cloud.common.api.exception.UnauthorizedException;
import rgo.cloud.common.api.exception.UnpredictableException;
import rgo.cloud.security.config.jwt.JwtProvider;

import java.util.Optional;

@Slf4j
public class AuthorizationFacade {
    private final ClientService clientService;
    private final ConfirmationTokenService tokenService;
    private final MailSender mailSender;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthorizationFacade(ClientService clientService,
                               ConfirmationTokenService tokenService,
                               MailSender mailSender,
                               JwtProvider jwtProvider,
                               AuthenticationManager authenticationManager) {
        this.clientService = clientService;
        this.tokenService = tokenService;
        this.mailSender = mailSender;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    public Client signUp(Client client) {
        Client saved = clientService.save(client);
        ConfirmationToken token = createToken(saved);

        mailSender.send(token);

        return saved;
    }

    private ConfirmationToken createToken(Client client) {
        ConfirmationToken token = ConfirmationToken.builder()
                .client(client)
                .build();

        return tokenService.save(token);
    }

    public AuthorizedClient signIn(Client client) {
        authenticate(client);
        Optional<Client> opt = clientService.findByMail(client.getMail());

        if (opt.isEmpty()) {
            String errorMsg = "The client no found during sign-in";
            log.error(errorMsg);
            throw new UnpredictableException(errorMsg);
        }

        return AuthorizedClient.builder()
                .client(opt.get())
                .token(jwtProvider.createToken(opt.get().getMail()))
                .build();
    }

    private void authenticate(Client client) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(client.getMail(), client.getPassword()));

        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new UnauthorizedException("The request contains invalid user data.");
        } catch (LockedException e) {
            throw new BannedException("The user is not activated.");
        }
    }

    public ConfirmationToken confirmAccount(Long clientId, String token) {
        Optional<ConfirmationToken> opt = tokenService.findByClientIdAndToken(clientId, token);

        if (opt.isEmpty()) {
            String errorMsg = "The token was not found during activation.";
            log.error(errorMsg);
            throw new EntityNotFoundException(errorMsg);
        }

        return opt.get();
    }

    public void activeClient(Long clientId) {
        Optional<Client> found = clientService.findById(clientId);

        if (found.isEmpty()) {
            String msg = "The client was not found during activation.";
            log.error(msg);
            throw new UnpredictableException(msg);
        }

        clientService.updateStatus(clientId, true);
    }
}
