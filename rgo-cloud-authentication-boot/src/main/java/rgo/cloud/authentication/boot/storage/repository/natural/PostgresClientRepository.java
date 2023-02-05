package rgo.cloud.authentication.boot.storage.repository.natural;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import rgo.cloud.authentication.boot.storage.query.ClientQuery;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.repository.ClientRepository;
import rgo.cloud.common.api.exception.UnpredictableException;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static rgo.cloud.authentication.boot.storage.repository.natural.mapper.ClientMapper.mapper;

@Slf4j
public class PostgresClientRepository implements ClientRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public PostgresClientRepository(DbTxManager tx) {
        this.jdbc = tx.jdbc();
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = jdbc.query(ClientQuery.findAll(), mapper);
        log.info("Size of clients: " + clients.size());

        return clients;
    }

    @Override
    public Optional<Client> findById(Long entityId) {
        MapSqlParameterSource params = new MapSqlParameterSource("entity_id", entityId);
        return first(
                jdbc.query(ClientQuery.findById(), params, mapper));
    }

    @Override
    public Optional<Client> findByMail(String mail) {
        MapSqlParameterSource params = new MapSqlParameterSource("mail", mail);
        return first(
                jdbc.query(ClientQuery.findByMail(), params, mapper));
    }

    @Override
    public boolean exists(Long entityId) {
        return findById(entityId).isPresent();
    }

    private Optional<Client> first(List<Client> list) {
        if (list.isEmpty()) {
            log.info("The client not found.");
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    @Override
    public Client save(Client client) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "surname", client.getSurname(),
                "name", client.getName(),
                "patronymic", client.getPatronymic(),
                "mail", client.getMail(),
                "password", client.getPassword()));

        int result = jdbc.update(ClientQuery.save(), params);
        Optional<Client> opt = findByMail(client.getMail());

        if (opt.isEmpty() || result != 1) {
            String errorMsg = "Client save error.";
            log.error(errorMsg);
            throw new UnpredictableException(errorMsg);
        }

        return opt.get();
    }

    @Override
    public Client update(Client client) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "entity_id", client.getEntityId(),
                "surname", client.getSurname(),
                "name", client.getName(),
                "patronymic", client.getPatronymic(),
                "password", client.getPassword(),
                "lmd", LocalDateTime.now(ZoneOffset.UTC)));

        int result = jdbc.update(ClientQuery.update(), params);
        Optional<Client> opt = findById(client.getEntityId());

        if (opt.isEmpty() || result != 1) {
            String errorMsg = "Client update error.";
            log.error(errorMsg);
            throw new UnpredictableException(errorMsg);
        }

        return opt.get();
    }

    @Override
    public Client updateStatus(Long entityId, boolean status) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "entity_id", entityId,
                "lmd", LocalDateTime.now(ZoneOffset.UTC),
                "active", status));

        int result = jdbc.update(ClientQuery.updateStatus(), params);
        Optional<Client> opt = findById(entityId);

        if (opt.isEmpty() || result != 1) {
            String errorMsg = "Client status update error.";
            log.error(errorMsg);
            throw new UnpredictableException(errorMsg);
        }

        return opt.get();
    }

    @Override
    public void resetPassword(String mail, String password) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "mail", mail,
                "password", password,
                "lmd", LocalDateTime.now(ZoneOffset.UTC)));

        int result = jdbc.update(ClientQuery.resetPassword(), params);
        if (result != 1) {
            String errorMsg = "Client password reset error.";
            log.error(errorMsg);
            throw new UnpredictableException(errorMsg);
        }
    }
}
