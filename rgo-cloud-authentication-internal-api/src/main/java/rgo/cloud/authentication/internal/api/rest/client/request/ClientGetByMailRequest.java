package rgo.cloud.authentication.internal.api.rest.client.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rgo.cloud.common.api.rest.Request;

import static rgo.cloud.common.api.util.ValidatorUtil.errorString;

@AllArgsConstructor
@Getter
@ToString
public class ClientGetByMailRequest implements Request {
    private final String mail;

    @Override
    public void validate() {
        errorString(mail, "mail");
    }
}
