package rgo.cloud.authentication.db.storage.repository.natural.mapper;

import org.springframework.jdbc.core.RowMapper;
import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;

public final class ClientEntryFailedMapper {
    private ClientEntryFailedMapper() {
    }

    public static final RowMapper<ClientEntryFailed> mapper = (rs, num) -> ClientEntryFailed.builder()
            .entityId(rs.getLong("ENTITY_ID"))
            .mail(rs.getString("MAIL"))
            .attempts(rs.getInt("ATTEMPTS"))
            .blockDate(rs.getTimestamp("BLOCK_DATE").toLocalDateTime())
            .build();
}
