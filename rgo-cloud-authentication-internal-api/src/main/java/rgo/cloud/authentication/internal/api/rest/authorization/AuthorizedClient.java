package rgo.cloud.authentication.internal.api.rest.authorization;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import rgo.cloud.authentication.internal.api.storage.Client;

@Getter
@Builder(toBuilder = true)
@ToString
public class AuthorizedClient {
    private final Client client;
    private final String token;
}
