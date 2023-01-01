package rgo.cloud.authentication.boot.api;

import org.springframework.web.bind.annotation.*;
import rgo.cloud.authentication.boot.api.decorator.ClientServiceDecorator;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientGetByIdRequest;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientGetByMailRequest;
import rgo.cloud.authentication.internal.api.rest.client.request.ClientUpdateRequest;
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

    @GetMapping(value = Endpoint.ENTITY_ID_VARIABLE, produces = JSON)
    public Response findById(@PathVariable Long entityId) {
        return execute(() -> service.findById(new ClientGetByIdRequest(entityId)));
    }

    @GetMapping(params = "mail", produces = JSON)
    public Response findByMail(@RequestParam("mail") String mail) {
        return execute(() -> service.findByMail(new ClientGetByMailRequest(mail)));
    }

    @PutMapping(consumes = JSON, produces = JSON)
    public Response update(@RequestBody ClientUpdateRequest rq) {
        return execute(() -> service.update(rq));
    }
}
