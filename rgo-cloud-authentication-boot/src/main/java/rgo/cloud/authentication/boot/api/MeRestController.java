package rgo.cloud.authentication.boot.api;

import org.springframework.web.bind.annotation.*;
import rgo.cloud.authentication.boot.api.decorator.ClientServiceDecorator;
import rgo.cloud.authentication.rest.api.client.request.ClientGetByIdRequest;
import rgo.cloud.authentication.rest.api.client.request.ClientGetByMailRequest;
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

    @GetMapping(params = "mail", produces = JSON)
    public Response findByMail(@RequestParam("mail") String mail) {
        return execute(() -> service.findByMail(new ClientGetByMailRequest(mail)));
    }
}
