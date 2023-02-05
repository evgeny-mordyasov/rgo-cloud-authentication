package rgo.cloud.authentication.boot.service.sender;

import rgo.cloud.authentication.boot.service.sender.model.MailMessage;

public interface MailSender {

    void send(MailMessage msg);
}
