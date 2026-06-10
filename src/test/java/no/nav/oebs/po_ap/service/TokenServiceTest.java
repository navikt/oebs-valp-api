package no.nav.oebs.po_ap.service;

import no.nav.oebs.po_ap.db.entity.TokenResponse;
import no.nav.oebs.po_ap.exception.SuppressedStackTraceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.net.URISyntaxException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "naisTokenEndpoint", "http://token-service:8080/api/v1/token");
        ReflectionTestUtils.setField(tokenService, "restClient", restClient);
    }

    @Nested
    class GetBaseUrlTests {

        @Test
        void getBaseUrl_withPort_returnsSchemeHostAndPort() throws URISyntaxException {
            String result = tokenService.getBaseUrl("http://host:8080/api/v1/token");

            assertEquals("http://host:8080", result);
        }

        @Test
        void getBaseUrl_withoutPort_returnsSchemeAndHost() throws URISyntaxException {
            String result = tokenService.getBaseUrl("https://token-service.intern.nav.no/api/v1/token");

            assertEquals("https://token-service.intern.nav.no", result);
        }

        @Test
        void getBaseUrl_invalidUrl_throwsURISyntaxException() {
            assertThrows(URISyntaxException.class,
                    () -> tokenService.getBaseUrl("ikke en url%%"));
        }
    }

    @Nested
    class FetchTokenTests {

        @Test
        void fetchToken_whenCacheIsValid_returnsCachedToken() {
            ReflectionTestUtils.setField(tokenService, "cachedToken", "cached-token-value");
            ReflectionTestUtils.setField(tokenService, "tokenExpiresAt", Instant.now().plusSeconds(300));

            String result = tokenService.fetchToken("azuread", "api://target/.default");

            assertEquals("cached-token-value", result);
            verifyNoInteractions(restClient);
        }

        @Test
        void fetchToken_whenCacheExpired_callsRestClientForNewToken() {
            ReflectionTestUtils.setField(tokenService, "cachedToken", "old-token");
            ReflectionTestUtils.setField(tokenService, "tokenExpiresAt", Instant.now().minusSeconds(60));

            when(restClient.post()).thenThrow(new RuntimeException("no server"));

            assertThrows(SuppressedStackTraceException.class,
                    () -> tokenService.fetchToken("azuread", "api://target/.default"));
        }

        @Test
        void fetchToken_whenRestClientThrows_throwsSuppressedStackTraceException() {
            when(restClient.post()).thenThrow(new RuntimeException("connection refused"));

            var ex = assertThrows(SuppressedStackTraceException.class,
                    () -> tokenService.fetchToken("azuread", "api://target/.default"));

            assertTrue(ex.getMessage().contains("Feil token"));
        }

    }
}
