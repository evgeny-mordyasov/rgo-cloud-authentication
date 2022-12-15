package rgo.cloud.authentication.internal.api.storage;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Client {
    private final Long entityId;
    private final String surname;
    private final String name;
    private final String patronymic;
    private final String mail;
    private final String password;
    private final Role role;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;
    private final boolean isActive;
}
