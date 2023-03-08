package rgo.cloud.authentication.boot.api.decorator;

import rgo.cloud.authentication.boot.facade.AuthorizationFacade;
import rgo.cloud.authentication.rest.api.authorization.AuthorizedClient;
import rgo.cloud.authentication.rest.api.authorization.HiddenClient;
import rgo.cloud.authentication.rest.api.authorization.request.*;
import rgo.cloud.authentication.rest.api.authorization.response.*;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.spring.annotation.Transactional;
import rgo.cloud.common.spring.annotation.Validate;

import static rgo.cloud.authentication.rest.converter.ClientConverter.convert;

@Validate
public class AuthorizationFacadeDecorator {
    private final AuthorizationFacade facade;

    public AuthorizationFacadeDecorator(AuthorizationFacade facade) {
        this.facade = facade;
    }

    @Transactional
    public Response signUp(AuthorizationSignUpRequest rq) {
        HiddenClient client = facade.signUp(convert(rq));
        return AuthorizationSignUpResponse.success(client);
    }

    public AuthorizationSignInResponse signIn(AuthorizationSignInRequest rq) {
        AuthorizedClient client = facade.signIn(convert(rq));
        return AuthorizationSignInResponse.success(client);
    }

    @Transactional
    public Response confirmAccount(AuthorizationConfirmAccountRequest rq) {
        facade.confirmAccount(rq.getClientId(), rq.getToken());
        return AuthorizationConfirmAccountResponse.success();
    }

    @Transactional
    public Response send(AuthorizationSendTokenRequest rq) {
        facade.send(rq.getClientId());
        return AuthorizationSendTokenResponse.success();
    }

    @Transactional
    public Response resetPassword(AuthorizationPasswordResetRequest rq) {
        facade.resetPassword(rq.getMail());
        return AuthorizationPasswordResetResponse.success();
    }
}
