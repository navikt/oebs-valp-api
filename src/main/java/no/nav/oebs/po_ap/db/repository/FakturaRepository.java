package no.nav.oebs.po_ap.db.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FakturaRepository {

    private final JdbcTemplate jdbcTemplate;

    public FakturaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateKvitteringStatus(String fakturaNummer) {
        String sql = "UPDATE XXRTV.XXRTV_INT_AP_TILTAK_MSG SET JSON_KVITT_SENT = 'Y' WHERE invoice_num = ?";
        jdbcTemplate.update(sql, fakturaNummer);
    }

}
