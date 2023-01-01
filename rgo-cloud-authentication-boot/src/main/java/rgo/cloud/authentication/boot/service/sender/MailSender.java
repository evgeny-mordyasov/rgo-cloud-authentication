package rgo.cloud.authentication.boot.service.sender;

import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

public interface MailSender {

    void send(ConfirmationToken token);
}
