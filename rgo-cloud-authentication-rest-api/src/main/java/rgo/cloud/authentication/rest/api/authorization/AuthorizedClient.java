package rgo.cloud.authentication.rest.api.authorization;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class AuthorizedClient {
    private final HiddenClient client;
    private final String token;
}
