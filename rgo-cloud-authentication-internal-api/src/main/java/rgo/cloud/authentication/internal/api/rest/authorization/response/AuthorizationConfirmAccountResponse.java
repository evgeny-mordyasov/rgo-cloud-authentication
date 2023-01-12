package rgo.cloud.authentication.internal.api.rest.authorization.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Builder
@Getter
@ToString
public class AuthorizationConfirmAccountResponse implements Response {
    private final Status status;

    public static AuthorizationConfirmAccountResponse success() {
        return AuthorizationConfirmAccountResponse.builder()
                .status(Status.success())
                .build();
    }
}
