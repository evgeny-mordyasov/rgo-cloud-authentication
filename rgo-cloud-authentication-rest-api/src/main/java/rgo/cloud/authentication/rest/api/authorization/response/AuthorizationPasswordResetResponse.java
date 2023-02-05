package rgo.cloud.authentication.rest.api.authorization.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Builder
@Getter
@ToString
public class AuthorizationPasswordResetResponse implements Response {
    private final Status status;

    public static AuthorizationPasswordResetResponse success() {
        return AuthorizationPasswordResetResponse.builder()
                .status(Status.success())
                .build();
    }
}
