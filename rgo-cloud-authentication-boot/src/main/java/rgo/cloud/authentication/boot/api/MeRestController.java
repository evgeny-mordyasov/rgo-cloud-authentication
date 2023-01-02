package rgo.cloud.authentication.boot.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rgo.cloud.authentication.boot.api.decorator.ClientServiceDecorator;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientGetByIdRequest;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.security.config.util.Endpoint;

import static rgo.cloud.common.api.util.RequestUtil.JSON;
import static rgo.cloud.common.api.util.RequestUtil.execute;

@RestController
@RequestMapping(Endpoint.Me.BASE_URL)
public class MeRestController {
    private final ClientServiceDecorator service;

    public MeRestController(ClientServiceDecorator service) {
        this.service = service;
    }

    @GetMapping(value = Endpoint.Me.CLIENT_ID_VARIABLE, produces = JSON)
    public Response findByClientId(@PathVariable Long clientId) {
        return execute(() -> service.findById(new ClientGetByIdRequest(clientId)));
    }
}
