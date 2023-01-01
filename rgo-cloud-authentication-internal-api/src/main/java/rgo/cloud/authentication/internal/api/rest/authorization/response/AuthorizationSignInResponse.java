package rgo.cloud.authentication.internal.api.rest.authorization.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.authentication.internal.api.rest.authorization.AuthorizedClient;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Builder
@Getter
@ToString
public class AuthorizationSignInResponse implements Response {
    private final Status status;
    private final AuthorizedClient object;

    public static AuthorizationSignInResponse success(AuthorizedClient client) {
        return AuthorizationSignInResponse.builder()
                .status(Status.success())
                .object(client)
                .build();
    }
}
