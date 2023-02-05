package rgo.cloud.authentication.rest.api.authorization.request;

import lombok.*;
import rgo.cloud.common.api.rest.Request;

import static rgo.cloud.common.api.util.ValidatorUtil.errorString;
import static rgo.cloud.common.api.util.ValidatorUtil.finish;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
public class AuthorizationSignUpRequest implements Request {
    private final String surname;
    private final String name;
    private final String patronymic;
    private final String mail;

    @ToString.Exclude
    private final String password;

    @Override
    public void validate() {
        errorString(surname, "surname");
        errorString(name, "name");
        errorString(patronymic, "patronymic");
        errorString(mail, "mail");
        errorString(password, "password");
        finish();
    }
}
