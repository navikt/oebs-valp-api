package no.nav.oebs.valp.api.refusjonskrav_info.v1;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.valp.Application;
import no.nav.oebs.valp.api.common.swagger.ValpSwagger;
import no.nav.oebs.valp.api.refusjonskrav.v1.RefusjonskravService;
import no.nav.oebs.valp.config.SwaggerConfig;
import no.nav.security.token.support.core.api.Protected;
import no.nav.security.token.support.core.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@Slf4j
@RestController
@Validated
@RequestMapping(path = "/api/v1")
@Tag(name = SwaggerConfig.VALP)
public class RefusjonskravInfoController {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private final RefusjonskravInfoService service;

	public RefusjonskravInfoController(RefusjonskravInfoService service) {
		this.service = service;
	}

	@Protected
	@ValpSwagger
	@GetMapping(path = "/refusjonskrav-info", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8") //APPLICATION_JSON_VALUE + ";charset=UTF-8")
	public String finnRefusjonskravtransaksjoner(
			@RequestParam(name = "org_id", defaultValue = "202") Integer org_id,
			@RequestParam(name = "po_number") @Parameter(description = "f.eks. 3170085") String po_number)
	{

		return " Api'et er under utvikling ..";
		// return service.finnRefusjonskravtransaksjoner(org_id, po_number);
	}
}