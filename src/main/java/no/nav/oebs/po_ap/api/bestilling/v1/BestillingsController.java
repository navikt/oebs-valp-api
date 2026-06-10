package no.nav.oebs.po_ap.api.bestilling.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.po_ap.api.common.swagger.PoApSwagger;
import no.nav.oebs.po_ap.config.SwaggerConfig;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
@Tag(name = SwaggerConfig.PO_AP, description = "Po-ap")
public class BestillingsController {

	private final BestillingsService service;

	public BestillingsController(BestillingsService service) { //,
			this.service = service;
	}

	@Protected
	@PostMapping(path = "/bestilling")
	@PoApSwagger
	public ResponseEntity<String> lagreBestilling(@RequestParam(defaultValue = "202") String org_id,
													 @Valid @RequestBody String message) {

		return ResponseEntity
				.ok()
				.body(service.lageBestilling(message)
				);
	}
}
