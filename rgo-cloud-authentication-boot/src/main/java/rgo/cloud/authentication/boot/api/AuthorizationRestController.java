package rgo.cloud.authentication.boot.api;

import org.springframework.web.bind.annotation.*;
import rgo.cloud.authentication.boot.api.decorator.AuthorizationFacadeDecorator;
import rgo.cloud.authentication.internal.api.rest.authorization.request.*;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.security.config.util.Endpoint;

import static rgo.cloud.common.api.util.RequestUtil.JSON;
import static rgo.cloud.common.api.util.RequestUtil.execute;

@RestController
@RequestMapping(Endpoint.Authorization.BASE_URL)
public class AuthorizationRestController {
    private final AuthorizationFacadeDecorator service;

    public AuthorizationRestController(AuthorizationFacadeDecorator service) {
        this.service = service;
    }

    @PostMapping(value = Endpoint.Authorization.SIGN_UP, consumes = JSON, produces = JSON)
    public Response signUp(@RequestBody AuthorizationSignUpRequest rq) {
        return execute(() -> service.signUp(rq));
    }

    @PostMapping(value = Endpoint.Authorization.SIGN_IN, consumes = JSON, produces = JSON)
    public Response signIn(@RequestBody AuthorizationSignInRequest rq) {
        return execute(() -> service.signIn(rq));
    }

    @PostMapping(value = Endpoint.Authorization.CONFIRM_ACCOUNT, produces = JSON)
    public Response confirmAccount(@RequestParam("clientId") Long clientId, @RequestParam("token") String token) {
        return execute(() ->
                service.confirmAccount(new AuthorizationConfirmAccountRequest(clientId, token)));
    }

    @PostMapping(value = Endpoint.Authorization.RESEND_TOKEN, produces = JSON)
    public Response resendToken(@RequestParam("clientId") Long clientId) {
        return execute(() -> service.resend(new AuthorizationResendTokenRequest(clientId)));
    }

    @PostMapping(value = Endpoint.Authorization.RESET_PASSWORD, produces = JSON)
    public Response resetPassword(@RequestParam("mail") String mail) {
        return execute(() -> service.resetPassword(new AuthorizationPasswordResetRequest(mail)));
    }
}
