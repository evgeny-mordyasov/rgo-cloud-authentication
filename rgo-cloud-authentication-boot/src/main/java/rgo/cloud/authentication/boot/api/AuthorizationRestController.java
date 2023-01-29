package rgo.cloud.authentication.boot.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rgo.cloud.authentication.boot.api.decorator.AuthorizationFacadeDecorator;
import rgo.cloud.authentication.internal.api.rest.authorization.request.*;
import rgo.cloud.authentication.internal.api.rest.authorization.response.*;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.security.config.jwt.properties.JwtProperties;
import rgo.cloud.security.config.util.Endpoint;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static rgo.cloud.common.api.util.RequestUtil.JSON;
import static rgo.cloud.common.api.util.RequestUtil.execute;

@RestController
@RequestMapping(Endpoint.Authorization.BASE_URL)
public class AuthorizationRestController {
    private final AuthorizationFacadeDecorator service;
    private final JwtProperties config;

    public AuthorizationRestController(AuthorizationFacadeDecorator service, JwtProperties config) {
        this.service = service;
        this.config = config;
    }

    @PostMapping(value = Endpoint.Authorization.SIGN_UP, consumes = JSON, produces = JSON)
    public Response signUp(@RequestBody AuthorizationSignUpRequest rq) {
        return execute(() -> service.signUp(rq));
    }

    @PostMapping(value = Endpoint.Authorization.SIGN_IN, consumes = JSON, produces = JSON)
    public Response signIn(@RequestBody AuthorizationSignInRequest rq, HttpServletResponse rs) {
        return execute(() ->  {
            AuthorizationSignInResponse result = service.signIn(rq);
            setAuthToken(result, rs);
            return result;
        });
    }

    private void setAuthToken(AuthorizationSignInResponse result, HttpServletResponse rs) {
        Cookie cookie = new Cookie(config.getAuthCookieName(), result.getObject().getToken());
        cookie.setPath(config.getPath());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(config.getExpirationHours() * 3600);
        rs.addCookie(cookie);
    }

    @PostMapping(value = Endpoint.Authorization.CONFIRM_ACCOUNT, produces = JSON)
    public Response confirmAccount(@RequestParam("clientId") Long clientId, @RequestParam("token") String token) {
        return execute(() ->
                service.confirmAccount(new AuthorizationConfirmAccountRequest(clientId, token)));
    }

    @PostMapping(value = Endpoint.Authorization.RESEND_TOKEN, produces = JSON)
    public Response resendToken(@RequestParam("clientId") Long clientId) {
        return execute(() -> service.resend(new AuthorizationResendTokenRequest(clientId)));
    }

    @PostMapping(value = Endpoint.Authorization.RESET_PASSWORD, produces = JSON)
    public Response resetPassword(@RequestParam("mail") String mail) {
        return execute(() -> service.resetPassword(new AuthorizationPasswordResetRequest(mail)));
    }
}
