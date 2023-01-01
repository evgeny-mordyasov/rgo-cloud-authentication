package rgo.cloud.authentication.boot.service.sender;

import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

public class MailSenderStub implements MailSender {
    public static String TOKEN;

    @Override
    public void send(ConfirmationToken token) {
        TOKEN = token.getToken();
    }
}
