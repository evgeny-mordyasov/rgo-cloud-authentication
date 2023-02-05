package rgo.cloud.authentication.rest.api.client.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Getter
@Builder
@ToString
public class ClientUpdateResponse implements Response {
    private final Status status;
    private final Client object;

    public static ClientUpdateResponse success(Client client) {
        return ClientUpdateResponse.builder()
                .status(Status.success())
                .object(client)
                .build();
    }
}
