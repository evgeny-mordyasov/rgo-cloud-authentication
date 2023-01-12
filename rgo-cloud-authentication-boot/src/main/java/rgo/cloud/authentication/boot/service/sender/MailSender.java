package rgo.cloud.authentication.boot.service.sender;

import rgo.cloud.authentication.internal.api.mail.MailMessage;

public interface MailSender {

    void send(MailMessage msg);
}
