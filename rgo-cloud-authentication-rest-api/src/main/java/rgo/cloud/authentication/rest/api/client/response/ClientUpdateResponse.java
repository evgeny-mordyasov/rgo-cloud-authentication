package rgo.cloud.authentication.rest.api.client.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.authentication.rest.api.authorization.HiddenClient;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.api.rest.Status;

@Getter
@Builder
@ToString
public class ClientUpdateResponse implements Response {
    private final Status status;
    private final HiddenClient object;

    public static ClientUpdateResponse success(HiddenClient client) {
        return ClientUpdateResponse.builder()
                .status(Status.success())
                .object(client)
                .build();
    }
}
