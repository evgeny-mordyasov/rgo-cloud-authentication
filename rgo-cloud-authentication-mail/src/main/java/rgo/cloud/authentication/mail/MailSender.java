package rgo.cloud.authentication.mail;

import rgo.cloud.authentication.mail.model.MailMessage;

public interface MailSender {

    void send(MailMessage msg);
}
