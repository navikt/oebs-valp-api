package no.nav.oebs.po_ap.db.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BestillingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BestillingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateKvitteringStatus(String bestillingsNummer) {
        String sql = "UPDATE XXRTV.XXRTV_INT_PO_TILTAK_MSG SET JSON_KVITT_SENT = 'Y' WHERE segment1 = ?";
        jdbcTemplate.update(sql, bestillingsNummer);
    }

}

