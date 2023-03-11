package rgo.cloud.authentication.boot.facade;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.*;
import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.service.ClientEntryFailedService;
import rgo.cloud.authentication.service.ClientService;
import rgo.cloud.authentication.service.ConfirmationTokenService;
import rgo.cloud.authentication.mail.MailSender;
import rgo.cloud.authentication.mail.model.MailMessage;
import rgo.cloud.authentication.rest.api.authorization.AuthorizedClient;
import rgo.cloud.authentication.rest.api.authorization.HiddenClient;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.common.api.exception.*;
import rgo.cloud.common.api.exception.IllegalStateException;
import rgo.cloud.security.config.jwt.JwtProvider;

import java.util.Optional;

import static rgo.cloud.authentication.rest.converter.ClientConverter.convert;
import static rgo.cloud.common.api.util.ExceptionUtil.unpredictableError;

@Slf4j
public class AuthorizationFacade {
    private final ClientService clientService;
    private final ClientEntryFailedService clientEntryFailedService;
    private final ConfirmationTokenService tokenService;
    private final MailSender mailSender;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthorizationFacade(ClientService clientService,
                               ClientEntryFailedService clientEntryFailedService,
                               ConfirmationTokenService tokenService,
                               MailSender mailSender,
                               JwtProvider jwtProvider,
                               AuthenticationManager authenticationManager) {
        this.clientService = clientService;
        this.clientEntryFailedService = clientEntryFailedService;
        this.tokenService = tokenService;
        this.mailSender = mailSender;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    public HiddenClient signUp(Client client) {
        Client saved = clientService.save(client);
        saveClientEntry(client);

        ConfirmationToken token = createToken(saved);
        sendRegistrationToken(token);

        return convert(saved);
    }

    private void saveClientEntry(Client client) {
        clientEntryFailedService.save(ClientEntryFailed.builder()
                .mail(client.getMail())
                .build());
    }

    private void sendRegistrationToken(ConfirmationToken token) {
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
            unpredictableError("The client no found during sign-in.");
        }

        return AuthorizedClient.builder()
                .client(convert(opt.get()))
                .token(jwtProvider.createToken(opt.get().getMail()))
                .build();
    }

    private void authenticate(Client client) {
        if (clientEntryFailedService.isBlocked(client.getMail())) {
            throw new BannedException("The client is blocked.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(client.getMail(), client.getPassword()));

            clientEntryFailedService.resetAttempts(client.getMail());
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            clientEntryFailedService.updateAttempts(client.getMail());
            throw new UnauthorizedException("The request contains invalid user data.");
        } catch (LockedException e) {
            throw new BannedException("The client is not verified.");
        }
    }

    public void confirmAccount(Long clientId, String token) {
        Optional<ConfirmationToken> opt = tokenService.findByClientId(clientId);

        if (opt.isEmpty()) {
            String errorMsg = "The client was not found during verification.";
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
            throw new IllegalStateException(errorMsg);
        }

        verifyClient(clientId);
    }

    private void verifyClient(Long clientId) {
        clientService.updateStatus(clientId, true);
    }

    public void send(Long clientId) {
        Optional<Client> opt = clientService.findById(clientId);

        if (opt.isEmpty()) {
            String msg = "The client not found by clientId.";
            log.error(msg);
            throw new EntityNotFoundException(msg);
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

    private void sendToken(ConfirmationToken token) {
        MailMessage msg = MailMessage.builder()
                .addressee(token.getClient().getMail())
                .header("Token")
                .message("To confirm your account, please enter the code in the field: " + token.getToken())
                .build();
        mailSender.send(msg);
    }

    public void resetPassword(String mail) {
        clientService.findByMail(mail).ifPresent(client -> {
            if (!client.isVerified()) {
                String errorMsg = "The client is not verified.";
                log.error(errorMsg);
                throw new BannedException(errorMsg);
            }

            String password = generatePassword();
            clientService.resetPassword(mail, password);
            sendPassword(mail, password);
        });
    }

    private String generatePassword() {
        return RandomStringUtils.random(15, true, true);
    }

    private void sendPassword(String mail, String password) {
        MailMessage msg = MailMessage.builder()
                .addressee(mail)
                .header("Password recovery")
                .message("Your new transport password for your account: " + password)
                .build();
        mailSender.send(msg);
    }
}
