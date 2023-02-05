package rgo.cloud.authentication.boot.api;

import org.springframework.web.bind.annotation.*;
import rgo.cloud.authentication.boot.api.decorator.ClientServiceDecorator;
import rgo.cloud.authentication.rest.api.client.request.ClientUpdateRequest;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.security.config.util.Endpoint;

import static rgo.cloud.common.api.util.RequestUtil.JSON;
import static rgo.cloud.common.api.util.RequestUtil.execute;

@RestController
@RequestMapping(Endpoint.Client.BASE_URL)
public class ClientRestController {
    private final ClientServiceDecorator service;

    public ClientRestController(ClientServiceDecorator service) {
        this.service = service;
    }

    @PutMapping(consumes = JSON, produces = JSON)
    public Response update(@RequestBody ClientUpdateRequest rq) {
        return execute(() -> service.update(rq));
    }
}
