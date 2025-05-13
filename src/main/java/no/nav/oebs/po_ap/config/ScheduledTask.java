package no.nav.oebs.po_ap.config;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import no.nav.oebs.po_ap.service.PostMeldingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    PostMeldingService postMeldingService;

    @Value("${scheduled.time}")
    private String scheduledTime;

    @Scheduled(cron = "${scheduled.time}")
    @SchedulerLock(
            name = "scheduledTask_process",
            lockAtLeastFor = "5m",
            lockAtMostFor = "15m"
    )
    public void process() {

        logger.info("Starter planlagt oppgave - Dette skal kun kjøres én gang på tvers av alle noder");

        String json_string = postMeldingService.postmelding();

        try {
            Thread.sleep(5000);
            if (!json_string.isEmpty()) {
                logger.info("Oppgave fullført, json string: {}", json_string);
            }

        } catch (InterruptedException e) {
            logger.error("Oppgaven avbrutt", e);
        }
    }
}
