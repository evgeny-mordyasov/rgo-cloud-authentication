package rgo.cloud.authentication.internal.api.rest.authorization.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Builder
@Getter
@ToString
public class AuthorizationResendTokenResponse implements Response {
    private final Status status;

    public static AuthorizationResendTokenResponse success() {
        return AuthorizationResendTokenResponse.builder()
                .status(Status.success())
                .build();
    }
}
