package no.nav.oebs.po_ap.api.token.v1;

// Create a REST Controller
import no.nav.oebs.po_ap.api.common.swagger.PoApSwagger;
import no.nav.oebs.po_ap.service.TokenService;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.api.Unprotected;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
public class TokenController {

    private final TokenService tokenService;

    @Value("${token.identityProvider}")
    private String identityProvider;

    @Value("${token.target}")
    private String target;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Protected
    @PoApSwagger
    @GetMapping("/token")
    public String getToken() {
        return tokenService.fetchToken(identityProvider, target);
    }
}
