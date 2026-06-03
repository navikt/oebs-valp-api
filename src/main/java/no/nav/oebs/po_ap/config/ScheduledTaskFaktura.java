package no.nav.oebs.po_ap.config;

import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.oebs.po_ap.service.FakturaServiceSched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class ScheduledTaskFaktura {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskFaktura.class);

    FakturaServiceSched fakturaServiceSched;

    @Scheduled(cron = "${scheduled.time.faktura}")
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
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Overføring kvitteringer avbrutt ..", e);
        } catch (Exception e) {
            logger.error("Overføring av fakturakvitteringer har feilet ..", e);
        }
    }
}
