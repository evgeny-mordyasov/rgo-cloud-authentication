package rgo.cloud.authentication.rest.api.authorization.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Builder
@Getter
@ToString
public class AuthorizationSendTokenResponse implements Response {
    private final Status status;

    public static AuthorizationSendTokenResponse success() {
        return AuthorizationSendTokenResponse.builder()
                .status(Status.success())
                .build();
    }
}
