package rgo.cloud.authentication.internal.api.rest.client.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Builder
@Getter
@ToString
public class ClientGetEntityResponse implements Response {
    private final Status status;
    private final Client object;

    public static ClientGetEntityResponse success(Client client) {
        return ClientGetEntityResponse.builder()
                .status(Status.success())
                .object(client)
                .build();
    }
}
