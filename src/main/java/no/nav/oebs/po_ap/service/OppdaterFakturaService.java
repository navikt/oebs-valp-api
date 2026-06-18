package no.nav.oebs.po_ap.service;

import no.nav.oebs.po_ap.config.FakturaDto;
import no.nav.oebs.po_ap.db.repository.FakturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.type.TypeReference;

import java.util.List;

@Service
public class OppdaterFakturaService {

    private final FakturaRepository repository;
    private final JsonMapper objectMapper;

    public OppdaterFakturaService(FakturaRepository repository, JsonMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public int updateKvitteringStatus(String jsonString) {
        List<FakturaDto> fakturaer = objectMapper.readValue(jsonString, new TypeReference<>() {});

        for (FakturaDto faktura : fakturaer) {
            repository.updateKvitteringStatus(faktura.getFakturaNummer());
        }

        return fakturaer.size();
    }
}
