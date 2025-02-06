package no.nav.oebs.valp.api.tilsagn.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.valp.Application;
import no.nav.oebs.valp.api.common.swagger.ValpSwagger;
import no.nav.oebs.valp.config.SwaggerConfig;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.VALP, description = "Valp")
public class TilsagnController {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private final TilsagnService service;

	public TilsagnController(TilsagnService service) { //,
			this.service = service;
	}

	@Protected
	@PostMapping(path = "/tilsagn")
	@ValpSwagger
	public ResponseEntity<String> lagreTilsagn(@RequestParam(defaultValue = "202") String org_id,
													 @Valid @RequestBody String message) {

		return ResponseEntity
				.ok()
				.body(service.lageTilsagn(message)
				);
	}
}
