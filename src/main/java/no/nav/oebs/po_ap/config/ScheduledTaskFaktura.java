package no.nav.oebs.po_ap.config;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.oebs.po_ap.service.FakturaServiceSched;
import no.nav.oebs.po_ap.service.OppdaterFakturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ScheduledTaskFaktura {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskFaktura.class);

    @Autowired
    FakturaServiceSched fakturaServiceSched;

    @Autowired
    OppdaterFakturaService oppdaterFakturaService;

    @Value("${scheduled.time.faktura}")
    private String scheduledTime;

    @Scheduled(cron = "${scheduled.time.faktura}")
    //@Scheduled(cron = "0 */3 * * * *")
    @SchedulerLock(
            name = "scheduledTaskFaktura_process",
            lockAtLeastFor = "2m",
            lockAtMostFor = "5m"
    )
    public void process() {

        try {
            fakturaServiceSched.sendFaktura();

            // Vent i 5 sekunder ..
            if (Objects.equals(fakturaServiceSched.STATUS, "OK")) {
                Thread.sleep(5000);
                // logger.info("Antall kvitteringer overført: {} ", oppdaterBestillingService.ANTALL);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Overføring kvitteringer avbrutt ..", e);
        } catch (Exception e) {
            logger.error("Overføring av kvitteringer har feilet ..", e);
        }
    }
}
