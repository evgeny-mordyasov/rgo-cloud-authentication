package rgo.cloud.authentication.boot.storage.repository.natural;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import rgo.cloud.authentication.boot.storage.query.ConfirmationTokenQuery;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;
import rgo.cloud.authentication.db.api.repository.ConfirmationTokenRepository;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static rgo.cloud.authentication.boot.storage.repository.natural.mapper.ConfirmationTokenMapper.mapper;
import static rgo.cloud.common.api.util.ExceptionUtil.unpredictableError;

@Slf4j
public class PostgresConfirmationTokenRepository implements ConfirmationTokenRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public PostgresConfirmationTokenRepository(DbTxManager tx) {
        this.jdbc = tx.jdbc();
    }

    @Override
    public Optional<ConfirmationToken> findByClientId(Long clientId) {
        MapSqlParameterSource params = new MapSqlParameterSource("client_id", clientId);
        return first(
                jdbc.query(ConfirmationTokenQuery.findByClientId(), params, mapper));
    }

    private Optional<ConfirmationToken> first(List<ConfirmationToken> list) {
        if (list.isEmpty()) {
            log.info("The token not found.");
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    @Override
    public ConfirmationToken save(ConfirmationToken ct) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "token", ct.getToken(),
                "client_id", ct.getClient().getEntityId(),
                "expiry_date", ct.getExpiryDate()));

        int result = jdbc.update(ConfirmationTokenQuery.save(), params);
        if (result != 1) {
            unpredictableError("Token save error.");
        }

        Optional<ConfirmationToken> opt = findByClientId(ct.getClient().getEntityId());
        if (opt.isEmpty()) {
            unpredictableError("Token save error during searching.");
        }

        return opt.get();
    }

    @Override
    public ConfirmationToken update(ConfirmationToken ct) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "token", ct.getToken(),
                "client_id", ct.getClient().getEntityId(),
                "expiry_date", ct.getExpiryDate()));

        int result = jdbc.update(ConfirmationTokenQuery.update(), params);
        if (result != 1) {
            unpredictableError("Token update error.");
        }

        Optional<ConfirmationToken> opt = findByClientId(ct.getClient().getEntityId());
        if (opt.isEmpty()) {
            unpredictableError("Token update error during searching.");
        }

        return opt.get();
    }
}
