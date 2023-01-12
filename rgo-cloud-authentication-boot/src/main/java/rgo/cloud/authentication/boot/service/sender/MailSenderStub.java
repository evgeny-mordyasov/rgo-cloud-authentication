package rgo.cloud.authentication.boot.service.sender;

import rgo.cloud.authentication.internal.api.mail.MailMessage;

public class MailSenderStub implements MailSender {
    public static String MESSAGE;

    @Override
    public void send(MailMessage msg) {
        MESSAGE = msg.getMessage();
    }
}
