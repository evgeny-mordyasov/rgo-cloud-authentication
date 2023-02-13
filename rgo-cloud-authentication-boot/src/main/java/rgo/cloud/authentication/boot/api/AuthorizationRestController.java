package rgo.cloud.authentication.boot.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rgo.cloud.authentication.boot.api.decorator.AuthorizationFacadeDecorator;
import rgo.cloud.authentication.rest.api.authorization.request.*;
import rgo.cloud.authentication.rest.api.authorization.response.AuthorizationLogoutResponse;
import rgo.cloud.authentication.rest.api.authorization.response.AuthorizationSignInResponse;
import rgo.cloud.common.api.rest.Response;
import rgo.cloud.security.config.jwt.properties.JwtProperties;
import rgo.cloud.security.config.util.Endpoint;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static rgo.cloud.common.spring.util.RequestUtil.JSON;
import static rgo.cloud.common.spring.util.RequestUtil.execute;

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
    public ResponseEntity<Response> signUp(@RequestBody AuthorizationSignUpRequest rq) {
        return execute(() -> service.signUp(rq));
    }

    @PostMapping(value = Endpoint.Authorization.SIGN_IN, consumes = JSON, produces = JSON)
    public ResponseEntity<Response> signIn(@RequestBody AuthorizationSignInRequest rq, HttpServletResponse rs) {
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

    @GetMapping(value = Endpoint.Authorization.LOGOUT, produces = JSON)
    public ResponseEntity<Response> logout(HttpServletResponse rs) {
        return execute(() ->  {
            clearAuthToken(rs);
            SecurityContextHolder.clearContext();
            return AuthorizationLogoutResponse.success();
        });
    }

    private void clearAuthToken(HttpServletResponse rs) {
        Cookie authCookie = new Cookie(config.getAuthCookieName(), StringUtils.EMPTY);
        authCookie.setPath(config.getPath());
        rs.addCookie(authCookie);
    }

    @PostMapping(value = Endpoint.Authorization.CONFIRM_ACCOUNT, produces = JSON)
    public ResponseEntity<Response> confirmAccount(@RequestParam("clientId") Long clientId, @RequestParam("token") String token) {
        return execute(() ->
                service.confirmAccount(new AuthorizationConfirmAccountRequest(clientId, token)));
    }

    @PostMapping(value = Endpoint.Authorization.RESEND_TOKEN, produces = JSON)
    public ResponseEntity<Response> resendToken(@RequestParam("clientId") Long clientId) {
        return execute(() -> service.resend(new AuthorizationResendTokenRequest(clientId)));
    }

    @PostMapping(value = Endpoint.Authorization.RESET_PASSWORD, produces = JSON)
    public ResponseEntity<Response> resetPassword(@RequestParam("mail") String mail) {
        return execute(() -> service.resetPassword(new AuthorizationPasswordResetRequest(mail)));
    }
}
