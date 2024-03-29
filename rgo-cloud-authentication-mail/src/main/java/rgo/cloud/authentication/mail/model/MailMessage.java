package rgo.cloud.authentication.mail.model;

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
