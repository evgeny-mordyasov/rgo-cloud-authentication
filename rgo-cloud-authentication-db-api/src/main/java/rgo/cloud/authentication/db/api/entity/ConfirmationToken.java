package rgo.cloud.authentication.db.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@ToString
public class ConfirmationToken {
    private final Long entityId;
    private final String token;
    private final LocalDateTime expiryDate;
    private final Client client;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
