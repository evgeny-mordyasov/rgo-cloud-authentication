package rgo.cloud.authentication.mail.api.properties;

public interface MailSenderProperties {
    String sender();

    String password();

    String host();

    int port();

    String protocol();

    int maxPoolSize();
}
