package rgo.cloud.authentication.internal.api.rest.authorization.request;

import lombok.*;
import rgo.cloud.common.api.rest.Request;

import static rgo.cloud.common.api.util.ValidatorUtil.errorObjectId;
import static rgo.cloud.common.api.util.ValidatorUtil.errorString;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@ToString
public class AuthorizationConfirmAccountRequest implements Request {
    private final Long clientId;
    private final String token;

    @Override
    public void validate() {
        errorObjectId(clientId, "clientId");
        errorString(token, "token");
    }
}
