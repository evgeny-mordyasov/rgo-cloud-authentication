package rgo.cloud.authentication.boot.storage.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import rgo.cloud.authentication.boot.storage.query.ClientQuery;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.common.api.exception.UnpredictableException;
import rgo.cloud.common.spring.storage.DbTxManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static rgo.cloud.authentication.boot.storage.repository.mapper.ClientMapper.mapper;

public class ClientRepository {
    private static final Logger log = LoggerFactory.getLogger(ClientRepository.class);

    private final DbTxManager tx;
    private final NamedParameterJdbcTemplate jdbc;

    public ClientRepository(DbTxManager tx) {
        this.tx = tx;
        this.jdbc = tx.jdbc();
    }

    public List<Client> findAll() {
        List<Client> clients = tx.tx(() -> jdbc.query(ClientQuery.findAll(), mapper));
        log.info("Size of clients: " + clients.size());

        return clients;
    }

    public Optional<Client> findById(Long entityId) {
        MapSqlParameterSource params = new MapSqlParameterSource("entity_id", entityId);
        return first(tx.tx(() ->
                jdbc.query(ClientQuery.findById(), params, mapper)));
    }

    public Optional<Client> findByMail(String mail) {
        MapSqlParameterSource params = new MapSqlParameterSource("mail", mail);
        return first(tx.tx(() ->
                jdbc.query(ClientQuery.findByMail(), params, mapper)));
    }

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

    public Client save(Client client) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "surname", client.getSurname(),
                "name", client.getName(),
                "patronymic", client.getPatronymic(),
                "mail", client.getMail(),
                "password", client.getPassword()));

        return tx.tx(() -> {
            jdbc.update(ClientQuery.save(), params);
            Optional<Client> opt = findByMail(client.getMail());

            if (opt.isEmpty()) {
                String errorMsg = "Client save error.";
                log.error(errorMsg);
                throw new UnpredictableException(errorMsg);
            }

            return opt.get();
        });
    }

    public Client update(Client client) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "entity_id", client.getEntityId(),
                "surname", client.getSurname(),
                "name", client.getName(),
                "patronymic", client.getPatronymic(),
                "password", client.getPassword(),
                "lmd", LocalDateTime.now(ZoneOffset.UTC)));

        return tx.tx(() -> {
            jdbc.update(ClientQuery.update(), params);
            Optional<Client> opt = findById(client.getEntityId());

            if (opt.isEmpty()) {
                String errorMsg = "Client update error.";
                log.error(errorMsg);
                throw new UnpredictableException(errorMsg);
            }

            return opt.get();
        });
    }

    public Client updateStatus(Long entityId, boolean status) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                "entity_id", entityId,
                "lmd", LocalDateTime.now(ZoneOffset.UTC),
                "active", status));

        return tx.tx(() -> {
            jdbc.update(ClientQuery.updateStatus(), params);
            Optional<Client> opt = findById(entityId);

            if (opt.isEmpty()) {
                String errorMsg = "Client status update error.";
                log.error(errorMsg);
                throw new UnpredictableException(errorMsg);
            }

            return opt.get();
        });
    }
}
