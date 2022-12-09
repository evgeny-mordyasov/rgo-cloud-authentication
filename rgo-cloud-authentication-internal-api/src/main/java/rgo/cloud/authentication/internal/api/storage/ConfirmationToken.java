package rgo.cloud.authentication.internal.api.storage;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConfirmationToken {
    private final Long entityId;
    private final String token;
    private final LocalDateTime expiryDate;
    private final Long clientId;
}
