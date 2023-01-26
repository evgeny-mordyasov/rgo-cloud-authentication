package rgo.cloud.authentication.internal.api.rest.authorization.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.rest.Request;

import static rgo.cloud.common.api.util.ValidatorUtil.errorObjectId;
import static rgo.cloud.common.api.util.ValidatorUtil.finish;

@AllArgsConstructor
@Getter
@ToString
public class AuthorizationResendTokenRequest implements Request {
    private final Long clientId;

    @Override
    public void validate() {
        errorObjectId(clientId, "clientId");
        finish();
    }
}
