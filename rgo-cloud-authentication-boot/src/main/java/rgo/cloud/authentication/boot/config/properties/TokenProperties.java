package rgo.cloud.authentication.boot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "module-properties.token")
public class TokenProperties {
    private final int lifetimeHours;

    public TokenProperties(int lifetimeHours) {
        this.lifetimeHours = lifetimeHours;
    }

    public int getLifetimeHours() {
        return lifetimeHours;
    }
}
