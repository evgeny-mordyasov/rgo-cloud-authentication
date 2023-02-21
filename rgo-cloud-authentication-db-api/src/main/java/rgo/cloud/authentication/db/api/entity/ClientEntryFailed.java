package rgo.cloud.authentication.db.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@ToString
public class ClientEntryFailed {
    private final Long entityId;
    private final String mail;
    private final int attempts;
    private final LocalDateTime blockDate;
}
