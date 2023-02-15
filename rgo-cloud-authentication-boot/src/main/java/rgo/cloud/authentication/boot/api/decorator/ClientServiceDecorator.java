package rgo.cloud.authentication.boot.api.decorator;

import rgo.cloud.authentication.service.ClientService;
import rgo.cloud.authentication.rest.api.client.request.ClientGetByIdRequest;
import rgo.cloud.authentication.rest.api.client.request.ClientGetByMailRequest;
import rgo.cloud.authentication.rest.api.client.request.ClientUpdateRequest;
import rgo.cloud.authentication.rest.api.client.response.ClientGetEntityResponse;
import rgo.cloud.authentication.rest.api.client.response.ClientUpdateResponse;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.common.api.rest.EmptySuccessfulResponse;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.common.spring.annotation.Validate;

import java.util.Optional;

import static rgo.cloud.authentication.rest.converter.ClientConverter.convert;

@Validate
public class ClientServiceDecorator {
    private final ClientService service;

    public ClientServiceDecorator(ClientService service) {
        this.service = service;
    }

    public Response findById(ClientGetByIdRequest rq) {
        Optional<Client> opt = service.findById(rq.getEntityId());
        return opt.isPresent()
                ? ClientGetEntityResponse.success(convert(opt.get()))
                : new EmptySuccessfulResponse();
    }

    public Response findByMail(ClientGetByMailRequest rq) {
        Optional<Client> opt = service.findByMail(rq.getMail());
        return opt.isPresent()
                ? ClientGetEntityResponse.success(opt.get())
                : new EmptySuccessfulResponse();
    }

    public ClientUpdateResponse update(ClientUpdateRequest rq) {
        Client client = service.update(convert(rq));
        return ClientUpdateResponse.success(convert(client));
    }
}
