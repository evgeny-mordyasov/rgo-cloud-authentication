package rgo.cloud.authentication.boot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "module-properties.mail")
public class MailSenderProperties {
    private final String sender;
    private final String password;
    private final String host;
    private final int port;
    private final String protocol;
    private final int maxPoolSize;

    public MailSenderProperties(String sender, String password, String host,
                                int port, String protocol, int maxPoolSize) {
        this.sender = sender;
        this.password = password;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.maxPoolSize = maxPoolSize;
    }

    public String getSender() {
        return sender;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }
}

