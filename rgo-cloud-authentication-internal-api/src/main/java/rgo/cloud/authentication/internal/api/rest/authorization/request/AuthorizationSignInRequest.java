package rgo.cloud.authentication.internal.api.rest.authorization.request;

import lombok.*;
import rgo.cloud.common.api.rest.Request;

import static rgo.cloud.common.api.util.ValidatorUtil.errorString;
import static rgo.cloud.common.api.util.ValidatorUtil.finish;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@ToString
public class AuthorizationSignInRequest implements Request {
    private final String mail;
    private final String password;

    @Override
    public void validate() {
        errorString(mail, "mail");
        errorString(password, "password");
        finish();
    }
}
