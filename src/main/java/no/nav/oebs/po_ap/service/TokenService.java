package no.nav.oebs.po_ap.service;

import jakarta.annotation.PostConstruct;
import no.nav.oebs.po_ap.exception.SuppressedStackTraceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;
import no.nav.oebs.po_ap.db.entity.TokenResponse;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TokenService {

    private RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private String cachedToken;
    private Instant tokenExpiresAt;

    @Value("${nais-token-endpoint}")
    private String naisTokenEndpoint;

    @Value("${base.url}")
    private String baseUrl;

    @PostConstruct
    public void init() throws URISyntaxException {
        String processedBaseUrl = getBaseUrl(naisTokenEndpoint); // Process the base URL
        this.restClient = RestClient.builder()
                .baseUrl(processedBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public String fetchToken(String identityProvider, String target) {
        if (cachedToken != null && tokenExpiresAt != null &&
                Instant.now().isBefore(tokenExpiresAt)) {
            logger.info("Returnerer bufret token");
            return cachedToken;
        }

        Map<String, String> requestBody = Map.of(
                "identity_provider", identityProvider,
                "target", target
        );

        logger.info("Ber om nytt token...");

        TokenResponse response; // = null;
        try {
            response = restClient.post()
                    .uri("/api/v1/token")
                    .body(requestBody)
                    .retrieve()
                    .body(TokenResponse.class);
        } catch (Exception e) {
            logger.error("Ingen token returnert: {}", e.getMessage());
            throw new SuppressedStackTraceException("An error occurred: " + e.getMessage());
        }

        assert response != null;
        cachedToken = response.getAccessToken();
        tokenExpiresAt = Instant.now().plusSeconds(response.getExpiresIn() - 30); // 30s buffer

        logger.info("Token mottatt og bufret, utløper kl {}", tokenExpiresAt);

        return cachedToken;
    }

    public String getBaseUrl(String fullUrl) throws URISyntaxException {
        URI uri = new URI(fullUrl);
        return uri.getScheme() + "://" + uri.getHost() + (uri.getPort() != -1 ? ":" + uri.getPort() : "");
    }
}