package rgo.cloud.authentication.boot.api.decorator.converter;

import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignInRequest;
import rgo.cloud.authentication.internal.api.rest.authorization.request.AuthorizationSignUpRequest;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientUpdateRequest;
import rgo.cloud.authentication.internal.api.storage.Client;

public class ClientConverter {
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
}
