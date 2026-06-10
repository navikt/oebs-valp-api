package no.nav.oebs.po_ap.db.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenResponseTest {

    @Test
    void setAndGetAccessToken() {
        var tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("mitt-token");

        assertEquals("mitt-token", tokenResponse.getAccessToken());
    }

    @Test
    void setAndGetExpiresIn() {
        var tokenResponse = new TokenResponse();
        tokenResponse.setExpiresIn(3600L);

        assertEquals(3600L, tokenResponse.getExpiresIn());
    }

    @Test
    void deserializeFromJson_mapsAccessTokenAndExpiresIn() throws Exception {
        String json = """
                {"access_token": "abc123", "expires_in": 1800}
                """;

        TokenResponse result = new ObjectMapper().readValue(json, TokenResponse.class);

        assertEquals("abc123", result.getAccessToken());
        assertEquals(1800L, result.getExpiresIn());
    }

    @Test
    void defaultValues_areNullAndZero() {
        var tokenResponse = new TokenResponse();

        assertNull(tokenResponse.getAccessToken());
        assertEquals(0L, tokenResponse.getExpiresIn());
    }
}
