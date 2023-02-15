package rgo.cloud.authentication.rest.api.client.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Builder
@Getter
@ToString
public class ClientGetEntityResponse implements Response {
    private final Status status;
    private final Object object;

    public static ClientGetEntityResponse success(Object client) {
        return ClientGetEntityResponse.builder()
                .status(Status.success())
                .object(client)
                .build();
    }
}
