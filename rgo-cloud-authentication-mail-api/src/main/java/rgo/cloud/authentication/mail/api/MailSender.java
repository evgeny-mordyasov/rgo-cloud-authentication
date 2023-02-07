package rgo.cloud.authentication.mail.api;

import rgo.cloud.authentication.mail.api.model.MailMessage;

public interface MailSender {

    void send(MailMessage msg);
}
