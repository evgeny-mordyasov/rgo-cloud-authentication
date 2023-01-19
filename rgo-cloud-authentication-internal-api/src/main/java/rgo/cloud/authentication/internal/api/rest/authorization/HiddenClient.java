package rgo.cloud.authentication.internal.api.rest.authorization;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.model.Role;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@ToString
public class HiddenClient {
    private final Long entityId;
    private final String surname;
    private final String name;
    private final String patronymic;
    private final String mail;
    private final Role role;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;
    private final boolean isActive;
}
