package no.nav.oebs.po_ap.config;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.oebs.po_ap.service.BestillingServiceSched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ScheduledTaskBestilling {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskBestilling.class);

    private final BestillingServiceSched bestillingServiceSched;

    public ScheduledTaskBestilling(BestillingServiceSched bestillingServiceSched) {
        this.bestillingServiceSched = bestillingServiceSched;
    }


    @Scheduled(cron = "${scheduled.time.bestilling}")
    @SchedulerLock(
            name = "scheduledTaskBestilling_process",
            lockAtLeastFor = "2m",
            lockAtMostFor = "5m"
    )
    public void process() {

        try {
            bestillingServiceSched.sendBestilling();

            if (Objects.equals(bestillingServiceSched.getStatus(), "OK")) {
                Thread.sleep(5000);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Overføring kvitteringer avbrutt ..", e);
        } catch (Exception e) {
            logger.error("Overføring av bestillingskvitteringer har feilet ..", e);
        }
    }
}