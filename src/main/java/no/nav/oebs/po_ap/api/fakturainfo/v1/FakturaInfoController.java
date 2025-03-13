package no.nav.oebs.po_ap.api.fakturainfo.v1;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.po_ap.Application;
import no.nav.oebs.po_ap.api.common.swagger.PoApSwagger;
import no.nav.oebs.po_ap.config.SwaggerConfig;
import no.nav.security.token.support.core.api.Protected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@Validated
@RequestMapping(path = "/api/v1")
@Tag(name = SwaggerConfig.PO_AP)
public class FakturaInfoController {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private final FakturaInfoService service;

	public FakturaInfoController(FakturaInfoService service) {
		this.service = service;
	}

	@Protected
	@PoApSwagger
	@GetMapping(path = "/fakturainfo", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8") //APPLICATION_JSON_VALUE + ";charset=UTF-8")
	public String finnRefusjonskravtransaksjoner(
			@RequestParam(name = "org_id", defaultValue = "202") Integer org_id,
			@RequestParam(name = "po_number") @Parameter(description = "f.eks. 3170085") String po_number)
	{

		return " Api'et er under utvikling ..";
		// return service.finnFakturaTransaksjoner(org_id, po_number);
	}
}