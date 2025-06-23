package no.nav.oebs.po_ap.config;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.oebs.po_ap.service.OppdaterBestillingService;
import no.nav.oebs.po_ap.service.BestillingServiceSched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ScheduledTaskBestilling {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskBestilling.class);

    @Autowired
    BestillingServiceSched bestillingServiceSched;

    @Autowired
    OppdaterBestillingService oppdaterBestillingService;

    @Value("${scheduled.time.bestilling}")
    private String scheduledTime;

    @Scheduled(cron = "${scheduled.time.bestilling}")
    // @Scheduled(cron = "0 */3 * * * *")
    @SchedulerLock(
            name = "scheduledTaskBestilling_process",
            lockAtLeastFor = "2m",
            lockAtMostFor = "5m"
    )
    public void process() {

        try {
            bestillingServiceSched.sendBestilling();

            // Vent i 5 sekunder ..
            if (Objects.equals(bestillingServiceSched.STATUS, "OK")) {
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