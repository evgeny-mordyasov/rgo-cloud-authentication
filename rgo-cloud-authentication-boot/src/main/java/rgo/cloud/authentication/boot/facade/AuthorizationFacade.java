package rgo.cloud.authentication.boot.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import rgo.cloud.authentication.boot.service.ClientService;
import rgo.cloud.authentication.boot.service.ConfirmationTokenService;
import rgo.cloud.authentication.boot.service.sender.MailSender;
import rgo.cloud.authentication.internal.api.mail.MailMessage;
import rgo.cloud.authentication.internal.api.rest.authorization.AuthorizedClient;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;
import rgo.cloud.common.api.exception.*;
import rgo.cloud.common.api.exception.IllegalStateException;
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
        sendToken(token);

        return saved;
    }

    private void sendToken(ConfirmationToken token) {
        MailMessage msg = MailMessage.builder()
                .addressee(token.getClient().getMail())
                .header("Complete registration")
                .message("To confirm your account, please enter the code in the registration field: " + token.getToken())
                .build();
        mailSender.send(msg);
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
            String errorMsg = "The client no found during sign-in.";
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

    public void confirmAccount(Long clientId, String token) {
        Optional<ConfirmationToken> opt = tokenService.findByClientIdAndToken(clientId);

        if (opt.isEmpty()) {
            String errorMsg = "The client was not found during activation.";
            log.error(errorMsg);
            throw new EntityNotFoundException(errorMsg);
        }

        if (!token.equals(opt.get().getToken())) {
            String errorMsg = "The token is invalid.";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        if (opt.get().isExpired()) {
            String errorMsg = "The token is expired.";
            log.error(errorMsg);
            throw new BannedException(errorMsg);
        }

        activeClient(clientId);
    }

    private void activeClient(Long clientId) {
        Optional<Client> opt = clientService.findById(clientId);

        if (opt.isEmpty()) {
            String msg = "The client was not found during activation.";
            log.error(msg);
            throw new UnpredictableException(msg);
        }

        clientService.updateStatus(opt.get().getEntityId(), true);
    }

    public void resend(Long clientId) {
        Optional<Client> opt = clientService.findById(clientId);

        if (opt.isEmpty()) {
            String msg = "The client not found by clientId.";
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }

        if (opt.get().isActive()) {
            String msg = "The client already activated.";
            log.error(msg);
            throw new ClientAlreadyActivatedException(msg);
        }

        ConfirmationToken token = updateToken(opt.get());
        sendToken(token);
    }

    private ConfirmationToken updateToken(Client client) {
        ConfirmationToken token = ConfirmationToken.builder()
                .client(client)
                .build();

        return tokenService.update(token);
    }
}
