package no.nav.oebs.po_ap.service;

import jakarta.annotation.PostConstruct;
import no.nav.oebs.po_ap.exception.SuppressedStackTraceException;
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
    private String cachedToken;
    private Instant tokenExpiresAt;

    @Value("${nais-token-endpoint}")
    private String naisTokenEndpoint;

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

            return cachedToken;
        }

        Map<String, String> requestBody = Map.of(
                "identity_provider", identityProvider,
                "target", target
        );

        TokenResponse response;
        try {
            response = restClient.post()
                    .uri("/api/v1/token")
                    .body(requestBody)
                    .retrieve()
                    .body(TokenResponse.class);
        } catch (Exception e) {
            throw new SuppressedStackTraceException("Feil token: " + e.getMessage());
        }

        assert response != null;
        cachedToken = response.getAccessToken();
        tokenExpiresAt = Instant.now().plusSeconds(response.getExpiresIn() - 30); // 30s buffer

        return cachedToken;
    }

    public String getBaseUrl(String fullUrl) throws URISyntaxException {
        URI uri = new URI(fullUrl);
        return uri.getScheme() + "://" + uri.getHost() + (uri.getPort() != -1 ? ":" + uri.getPort() : "");
    }
}