package rgo.cloud.authentication.boot.storage.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import rgo.cloud.authentication.boot.storage.query.ConfirmationTokenQuery;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;
import rgo.cloud.common.api.exception.UnpredictableException;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static rgo.cloud.authentication.boot.storage.repository.mapper.ConfirmationTokenMapper.mapper;

@Slf4j
public class ConfirmationTokenRepository {
    private final DbTxManager tx;
    private final NamedParameterJdbcTemplate jdbc;

    public ConfirmationTokenRepository(DbTxManager tx) {
        this.tx = tx;
        this.jdbc = tx.jdbc();
    }

    public Optional<ConfirmationToken> findByClientId(Long clientId) {
        MapSqlParameterSource params = new MapSqlParameterSource("client_id", clientId);
        return first(tx.tx(() ->
                jdbc.query(ConfirmationTokenQuery.findByClientId(), params, mapper)));
    }

    private Optional<ConfirmationToken> first(List<ConfirmationToken> list) {
        if (list.isEmpty()) {
            log.info("The token not found.");
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    public ConfirmationToken save(ConfirmationToken ct) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "token", ct.getToken(),
                "client_id", ct.getClient().getEntityId(),
                "expiry_date", ct.getExpiryDate()));

        return tx.tx(() -> {
            jdbc.update(ConfirmationTokenQuery.save(), params);
            Optional<ConfirmationToken> opt = findByClientId(ct.getClient().getEntityId());

            if (opt.isEmpty()) {
                String errorMsg = "Token save error.";
                log.error(errorMsg);
                throw new UnpredictableException(errorMsg);
            }

            return opt.get();
        });
    }

    public ConfirmationToken update(ConfirmationToken ct) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "token", ct.getToken(),
                "client_id", ct.getClient().getEntityId(),
                "expiry_date", ct.getExpiryDate()));

        return tx.tx(() -> {
            jdbc.update(ConfirmationTokenQuery.update(), params);
            Optional<ConfirmationToken> opt = findByClientId(ct.getClient().getEntityId());

            if (opt.isEmpty()) {
                String errorMsg = "Token update error.";
                log.error(errorMsg);
                throw new UnpredictableException(errorMsg);
            }

            return opt.get();
        });
    }
}
