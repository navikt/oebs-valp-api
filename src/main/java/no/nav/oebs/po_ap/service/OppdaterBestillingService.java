package no.nav.oebs.po_ap.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.oebs.po_ap.config.BestillingDto;
import no.nav.oebs.po_ap.db.repository.BestillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

    @Service
    public class OppdaterBestillingService {

        private final BestillingRepository repository;
        private final ObjectMapper objectMapper;
        private final Logger logger = LoggerFactory.getLogger(TokenService.class);

        public OppdaterBestillingService(BestillingRepository repository, ObjectMapper objectMapper) {
            this.repository = repository;
            this.objectMapper = objectMapper;
        }

        @Transactional
        public int updateKvitteringStatus(String jsonString) throws IOException {
            List<BestillingDto> bestillinger = objectMapper.readValue(jsonString, new TypeReference<List<BestillingDto>>() {});

            for (BestillingDto bestilling : bestillinger) {
                repository.updateKvitteringStatus(bestilling.getBestillingsNummer());
            }

            return bestillinger.size();
    }
}
