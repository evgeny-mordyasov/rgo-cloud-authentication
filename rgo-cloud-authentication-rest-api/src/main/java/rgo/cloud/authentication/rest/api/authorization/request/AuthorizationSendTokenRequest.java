package rgo.cloud.authentication.rest.api.authorization.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.rest.Request;

import static rgo.cloud.common.api.util.ValidatorUtil.errorObjectId;

@AllArgsConstructor
@Getter
@ToString
public class AuthorizationSendTokenRequest implements Request {
    private final Long clientId;

    @Override
    public void validate() {
        errorObjectId(clientId, "clientId");
    }
}
