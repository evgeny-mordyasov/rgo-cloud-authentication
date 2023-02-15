package rgo.cloud.authentication.db.api.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.model.Role;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@ToString
public class Client {
    private final Long entityId;
    private final String surname;
    private final String name;
    private final String patronymic;
    private final String mail;

    @ToString.Exclude
    private final String password;

    private final Role role;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;
    private final boolean isVerified;
}
