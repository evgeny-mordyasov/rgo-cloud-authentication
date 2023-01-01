package rgo.cloud.authentication.boot.api.decorator;

import rgo.cloud.authentication.boot.facade.AuthorizationFacade;
import rgo.cloud.authentication.internal.api.rest.authorization.AuthorizedClient;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationConfirmAccountRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.response.AuthorizationConfirmAccountResponse;
import rgo.cloud.authentication.internal.api.rest.authorization.response.AuthorizationSignInResponse;
import rgo.cloud.authentication.internal.api.rest.authorization.response.AuthorizationSignUpResponse;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.spring.aspect.Validate;

import static rgo.cloud.authentication.boot.api.decorator.converter.ClientConverter.convert;

@Validate
public class AuthorizationFacadeDecorator {
    private final AuthorizationFacade facade;

    public AuthorizationFacadeDecorator(AuthorizationFacade facade) {
        this.facade = facade;
    }

    public AuthorizationSignUpResponse signUp(AuthorizationSignUpRequest rq) {
        Client client = facade.signUp(convert(rq));
        return AuthorizationSignUpResponse.success(client);
    }

    public AuthorizationSignInResponse signIn(AuthorizationSignInRequest rq) {
        AuthorizedClient client = facade.signIn(convert(rq));
        return AuthorizationSignInResponse.success(client);
    }

    public Response confirmAccount(AuthorizationConfirmAccountRequest rq) {
        ConfirmationToken token = facade.confirmAccount(rq.getClientId(), rq.getToken());

        if (token.isExpired()) {
            return AuthorizationConfirmAccountResponse.banned();
        }

        facade.activeClient(rq.getClientId());

        return AuthorizationConfirmAccountResponse.success();
    }
}
