package rgo.cloud.authentication.boot.storage.repository.natural.mapper;

import org.springframework.jdbc.core.RowMapper;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.common.api.model.Role;

public final class ClientMapper {
    private ClientMapper() {
    }

    public static final RowMapper<Client> mapper = (rs, num) -> Client.builder()
            .entityId(rs.getLong("ENTITY_ID"))
            .surname(rs.getString("SURNAME"))
            .name(rs.getString("NAME"))
            .patronymic(rs.getString("PATRONYMIC"))
            .mail(rs.getString("MAIL"))
            .password(rs.getString("PASSWORD"))
            .createdDate(rs.getTimestamp("CREATED_DATE").toLocalDateTime())
            .lastModifiedDate(rs.getTimestamp("LAST_MODIFIED_DATE").toLocalDateTime())
            .isActive(rs.getBoolean("IS_ACTIVE"))
            .role(Role.valueOf(rs.getString("ROLE")))
            .build();
}
