package rgo.cloud.authentication.db.storage.repository.natural;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.db.api.repository.ClientEntryFailedRepository;
import rgo.cloud.authentication.db.storage.query.ClientEntryFailedQuery;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static rgo.cloud.authentication.db.storage.repository.natural.mapper.ClientEntryFailedMapper.mapper;
import static rgo.cloud.common.api.util.ExceptionUtil.unpredictableError;

@Slf4j
public class PostgresClientEntryFailedRepository implements ClientEntryFailedRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public PostgresClientEntryFailedRepository(DbTxManager tx) {
        this.jdbc = tx.jdbc();
    }

    @Override
    public Optional<ClientEntryFailed> findByMail(String mail) {
        MapSqlParameterSource params = new MapSqlParameterSource("mail", mail);
        return first(jdbc.query(ClientEntryFailedQuery.findByMail(), params, mapper));
    }

    @Override
    public ClientEntryFailed save(ClientEntryFailed cef) {
        MapSqlParameterSource params = new MapSqlParameterSource("mail", cef.getMail());

        int result = jdbc.update(ClientEntryFailedQuery.save(), params);
        if (result != 1) {
            unpredictableError("ClientEntryFailed save error.");
        }

        Optional<ClientEntryFailed> opt = findByMail(cef.getMail());
        if (opt.isEmpty()) {
            unpredictableError("ClientEntryFailed save error during searching.");
        }

        return opt.get();
    }

    private Optional<ClientEntryFailed> first(List<ClientEntryFailed> list) {
        if (list.isEmpty()) {
            log.info("The clientEntryFailed not found.");
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    @Override
    public void updateAttempts(int attempts, String mail) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "attempts", attempts,
                "mail", mail));

        int result = jdbc.update(ClientEntryFailedQuery.updateAttempts(), params);
        if (result != 1) {
            unpredictableError("Update attempts failed.");
        }
    }

    @Override
    public void resetAttempts(String mail) {
        MapSqlParameterSource params = new MapSqlParameterSource("mail", mail);

        int result = jdbc.update(ClientEntryFailedQuery.resetAttempts(), params);
        if (result != 1) {
            unpredictableError("Reset attempts failed.");
        }
    }

    @Override
    public void block(String mail) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "block_date", LocalDateTime.now(ZoneOffset.UTC),
                "mail", mail));

        int result = jdbc.update(ClientEntryFailedQuery.block(), params);
        if (result != 1) {
            unpredictableError("Block client entry failed.");
        }
    }
}
