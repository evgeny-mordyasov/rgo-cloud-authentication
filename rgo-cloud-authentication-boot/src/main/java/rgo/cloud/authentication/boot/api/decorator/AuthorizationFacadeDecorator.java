package rgo.cloud.authentication.boot.api.decorator;

import rgo.cloud.authentication.boot.facade.AuthorizationFacade;
import rgo.cloud.authentication.internal.api.rest.authorization.AuthorizedClient;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationConfirmAccountRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationResendTokenRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.response.AuthorizationConfirmAccountResponse;
import rgo.cloud.authentication.internal.api.rest.authorization.response.AuthorizationResendTokenResponse;
import rgo.cloud.authentication.internal.api.rest.authorization.response.AuthorizationSignInResponse;
import rgo.cloud.authentication.internal.api.rest.authorization.response.AuthorizationSignUpResponse;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.spring.annotation.Transactional;
import rgo.cloud.common.spring.annotation.Validate;

import static rgo.cloud.authentication.boot.api.decorator.converter.ClientConverter.convert;

@Validate
public class AuthorizationFacadeDecorator {
    private final AuthorizationFacade facade;

    public AuthorizationFacadeDecorator(AuthorizationFacade facade) {
        this.facade = facade;
    }

    @Transactional
    public Response signUp(AuthorizationSignUpRequest rq) {
        Client client = facade.signUp(convert(rq));
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
    public Response resend(AuthorizationResendTokenRequest rq) {
        facade.resend(rq.getClientId());
        return AuthorizationResendTokenResponse.success();
    }
}
