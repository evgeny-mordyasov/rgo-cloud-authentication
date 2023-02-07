package rgo.cloud.authentication.boot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import rgo.cloud.authentication.mail.api.properties.MailSenderProperties;

@ConstructorBinding
@ConfigurationProperties(prefix = "module-properties.mail")
public class MailProperties implements MailSenderProperties {
    private final String sender;
    private final String password;
    private final String host;
    private final int port;
    private final String protocol;
    private final int maxPoolSize;

    public MailProperties(String sender, String password, String host,
                                int port, String protocol, int maxPoolSize) {
        this.sender = sender;
        this.password = password;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    public String sender() {
        return sender;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public String protocol() {
        return protocol;
    }

    @Override
    public int maxPoolSize() {
        return maxPoolSize;
    }
}

