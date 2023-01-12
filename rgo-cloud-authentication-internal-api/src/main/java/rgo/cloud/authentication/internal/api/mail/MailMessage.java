package rgo.cloud.authentication.internal.api.mail;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class MailMessage {
    private final String addressee;
    private final String header;
    private final String message;
}
