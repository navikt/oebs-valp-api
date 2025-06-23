package no.nav.oebs.po_ap.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.oebs.po_ap.config.FakturaDto;
import no.nav.oebs.po_ap.db.repository.FakturaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class OppdaterFakturaService {

    private final FakturaRepository repository;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);

    public OppdaterFakturaService(FakturaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public int updateKvitteringStatus(String jsonString) throws IOException {
        List<FakturaDto> fakturaer = objectMapper.readValue(jsonString, new TypeReference<List<FakturaDto>>() {});

        for (FakturaDto faktura : fakturaer) {
            repository.updateKvitteringStatus(faktura.getFakturaNummer());
        }

        return fakturaer.size();
    }
}
