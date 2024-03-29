package rgo.cloud.authentication.rest.api.client.request;

import lombok.*;
import rgo.cloud.common.api.rest.Request;

import static rgo.cloud.common.api.util.ValidatorUtil.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@ToString
public class ClientUpdateRequest implements Request {
    private final Long entityId;
    private final String surname;
    private final String name;
    private final String patronymic;
    private final String password;

    @Override
    public void validate() {
        errorEntityId(entityId);
        errorString(surname, "surname");
        errorString(name, "name");
        errorString(patronymic, "patronymic");
        errorString(password, "password");
    }
}
