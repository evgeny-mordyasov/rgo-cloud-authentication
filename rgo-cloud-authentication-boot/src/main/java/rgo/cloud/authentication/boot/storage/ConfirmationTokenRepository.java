package rgo.cloud.authentication.boot.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import rgo.cloud.authentication.boot.config.properties.TokenProperties;
import rgo.cloud.authentication.boot.storage.query.ConfirmationTokenQuery;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfirmationTokenRepository {
    private static final Logger log = LoggerFactory.getLogger(ConfirmationTokenRepository.class);

    private final DbTxManager tx;
    private final NamedParameterJdbcTemplate jdbc;
    private final TokenProperties config;

    public ConfirmationTokenRepository(DbTxManager tx, TokenProperties config) {
        this.tx = tx;
        this.jdbc = tx.jdbc();
        this.config = config;
    }

    public Optional<ConfirmationToken> findByClientIdAndToken(Long clientId, String token) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "token", token,
                "client_id", clientId));

        return first(tx.tx(() ->
                jdbc.query(ConfirmationTokenQuery.findByClientIdAndToken(), params, mapper)));
    }

    private Optional<ConfirmationToken> first(List<ConfirmationToken> list) {
        if (list.isEmpty()) {
            log.info("The client not found.");
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    public ConfirmationToken save(ConfirmationToken ct) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "token", ct.getToken(),
                "client_id", ct.getClientId(),
                "expiry_date", expiryDateToken()));

        return tx.tx(() -> {
            jdbc.update(ConfirmationTokenQuery.save(), params);
            Optional<ConfirmationToken> opt = findByClientIdAndToken(ct.getClientId(), ct.getToken());

            if (opt.isEmpty()) {
                String errorMsg = "Token save error.";
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            return opt.get();
        });
    }

    private LocalDateTime expiryDateToken() {
        return LocalDateTime.now()
                .plusHours(config.getLifetimeHours());
    }

    private static final RowMapper<ConfirmationToken> mapper = (rs, num) -> ConfirmationToken.builder()
            .entityId(rs.getLong("ENTITY_ID"))
            .token(rs.getString("TOKEN"))
            .expiryDate(rs.getTimestamp("EXPIRY_DATE").toLocalDateTime())
            .clientId(rs.getLong("CLIENT_ID"))
            .build();
}
