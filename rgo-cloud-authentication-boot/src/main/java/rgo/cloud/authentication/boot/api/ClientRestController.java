package rgo.cloud.authentication.boot.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rgo.cloud.authentication.boot.api.decorator.ClientServiceDecorator;
import rgo.cloud.authentication.rest.api.client.request.ClientUpdateRequest;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.security.config.util.Endpoint;

import static rgo.cloud.common.spring.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.RequestUtil.execute;

@Hidden
@RestController
@RequestMapping(Endpoint.Client.BASE_URL)
public class ClientRestController {
    private final ClientServiceDecorator service;

    public ClientRestController(ClientServiceDecorator service) {
        this.service = service;
    }

    @PutMapping(consumes = JSON, produces = JSON)
    public ResponseEntity<Response> update(@RequestBody ClientUpdateRequest rq) {
        return execute(() -> service.update(rq));
    }
}
