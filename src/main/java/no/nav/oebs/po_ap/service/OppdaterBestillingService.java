package no.nav.oebs.po_ap.service;

import no.nav.oebs.po_ap.config.BestillingDto;
import no.nav.oebs.po_ap.db.repository.BestillingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;

    @Service
    public class OppdaterBestillingService {

        private final BestillingRepository repository;
        private final JsonMapper objectMapper;

        public OppdaterBestillingService(BestillingRepository repository, JsonMapper objectMapper) {
            this.repository = repository;
            this.objectMapper = objectMapper;
        }

        @Transactional
        public int updateKvitteringStatus(String jsonString) {
            List<BestillingDto> bestillinger = objectMapper.readValue(jsonString, new TypeReference<>() {});

            for (BestillingDto bestilling : bestillinger) {
                repository.updateKvitteringStatus(bestilling.getBestillingsNummer());
            }

            return bestillinger.size();
    }
}
