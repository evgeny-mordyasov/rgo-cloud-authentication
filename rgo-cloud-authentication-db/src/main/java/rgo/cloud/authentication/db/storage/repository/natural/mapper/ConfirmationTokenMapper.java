package rgo.cloud.authentication.db.storage.repository.natural.mapper;

import org.springframework.jdbc.core.RowMapper;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.common.api.model.Role;

public final class ConfirmationTokenMapper {
    private ConfirmationTokenMapper() {
    }

    public static final RowMapper<ConfirmationToken> mapper = (rs, num) -> ConfirmationToken.builder()
            .entityId(rs.getLong("ENTITY_ID"))
            .token(rs.getString("TOKEN"))
            .expiryDate(rs.getTimestamp("EXPIRY_DATE").toLocalDateTime())
            .client(Client.builder()
                    .entityId(rs.getLong("CLIENT_ID"))
                    .surname(rs.getString("CLIENT_SURNAME"))
                    .name(rs.getString("CLIENT_NAME"))
                    .patronymic(rs.getString("CLIENT_PATRONYMIC"))
                    .mail(rs.getString("CLIENT_MAIL"))
                    .password(rs.getString("CLIENT_PASSWORD"))
                    .createdDate(rs.getTimestamp("CLIENT_CREATED_DATE").toLocalDateTime())
                    .lastModifiedDate(rs.getTimestamp("CLIENT_LAST_MODIFIED_DATE").toLocalDateTime())
                    .isVerified(rs.getBoolean("CLIENT_IS_VERIFIED"))
                    .role(Role.of(rs.getString("CLIENT_ROLE")))
                    .build())
            .build();
}
