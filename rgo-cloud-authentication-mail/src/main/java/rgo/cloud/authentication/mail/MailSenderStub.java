package rgo.cloud.authentication.mail;

import rgo.cloud.authentication.mail.model.MailMessage;

public class MailSenderStub implements MailSender {
    public static String MESSAGE;

    @Override
    public void send(MailMessage msg) {
        MESSAGE = msg.getMessage();
    }
}
