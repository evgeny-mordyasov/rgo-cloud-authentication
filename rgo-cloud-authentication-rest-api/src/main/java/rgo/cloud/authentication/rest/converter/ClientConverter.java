package rgo.cloud.authentication.rest.converter;

import rgo.cloud.authentication.rest.api.authorization.HiddenClient;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.rest.api.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.authentication.rest.api.client.request.ClientUpdateRequest;
import rgo.cloud.authentication.db.api.entity.Client;

public final class ClientConverter {
    private ClientConverter() {
    }

    public static Client convert(AuthorizationSignUpRequest rq) {
        return Client.builder()
                .surname(rq.getSurname())
                .name(rq.getName())
                .patronymic(rq.getPatronymic())
                .mail(rq.getMail())
                .password(rq.getPassword())
                .build();
    }

    public static Client convert(AuthorizationSignInRequest rq) {
        return Client.builder()
                .mail(rq.getMail())
                .password(rq.getPassword())
                .build();
    }

    public static Client convert(ClientUpdateRequest rq) {
        return Client.builder()
                .entityId(rq.getEntityId())
                .surname(rq.getSurname())
                .name(rq.getName())
                .patronymic(rq.getPatronymic())
                .password(rq.getPassword())
                .build();
    }

    public static HiddenClient convert(Client client) {
        return HiddenClient.builder()
                .entityId(client.getEntityId())
                .surname(client.getSurname())
                .name(client.getName())
                .patronymic(client.getPatronymic())
                .mail(client.getMail())
                .createdDate(client.getCreatedDate())
                .lastModifiedDate(client.getLastModifiedDate())
                .role(client.getRole())
                .isActive(client.isActive())
                .build();
    }
}
