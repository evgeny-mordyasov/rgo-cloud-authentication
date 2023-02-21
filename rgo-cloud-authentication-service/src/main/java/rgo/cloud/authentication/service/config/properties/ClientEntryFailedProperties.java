package rgo.cloud.authentication.service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "module-properties.entry-failed")
public class ClientEntryFailedProperties {
    private final int maxFailedAttempts;
    private final long blockTimeMs;

    public ClientEntryFailedProperties(int maxFailedAttempts, long blockTimeMs) {
        this.maxFailedAttempts = maxFailedAttempts;
        this.blockTimeMs = blockTimeMs;
    }

    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public long getBlockTimeMs() {
        return blockTimeMs;
    }
}
