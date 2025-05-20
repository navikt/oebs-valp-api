package no.nav.oebs.po_ap.config;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.oebs.po_ap.service.PostMeldingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import no.nav.oebs.po_ap.config.SwaggerConfig;

import java.util.Objects;

@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    PostMeldingService postMeldingService;

    @Value("${scheduled.time}")
    private String scheduledTime;

    @Scheduled(cron = "${scheduled.time}")
    // @Scheduled(cron = "0 */3 * * * *")
    @SchedulerLock(
            name = "scheduledTask_process",
            lockAtLeastFor = "5m",
            lockAtMostFor = "15m"
    )
    public void process() {

        logger.info("Starter planlagt oppgave - Dette skal kun kjøres én gang på tvers av alle noder");

        try {
            postMeldingService.postmelding();

            // Vent i 5 sekunder ..
            if (Objects.equals(postMeldingService.STATUS, "OK")) {
                Thread.sleep(5000);
                logger.info("Overføring av bestillingskvitteringer er fullført");
            } else {
                logger.info("Ingen data funnet");
            }
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            logger.error("Overføring av bestillingskvitteringer avbrutt", e);
        } catch (Exception e) {
            // Catch other exceptions to prevent scheduled task from failing silently
            logger.error("Overføring av bestillingskvitteringer har feilet", e);
        }
    }
}