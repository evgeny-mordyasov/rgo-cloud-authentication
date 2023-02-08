package rgo.cloud.authentication.service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "module-properties.token")
public class TokenProperties {
    private final int lifetimeHours;
    private final int tokenLength;

    public TokenProperties(int lifetimeHours) {
        this.lifetimeHours = lifetimeHours;
        this.tokenLength = 6;
    }

    public int getLifetimeHours() {
        return lifetimeHours;
    }

    public int getTokenLength() {
        return tokenLength;
    }
}
