package no.nav.oebs.valp.db.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import no.nav.oebs.valp.db.entity.KallLogg;
import org.springframework.stereotype.Repository;

@Repository
public class KallLoggRepositoryImpl implements KallLoggRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void pingKallLogg() {
		entityManager.createQuery("SELECT k FROM KallLogg k WHERE kall_logg_id = 0", KallLogg.class) //
				.getResultList();
	}
}
