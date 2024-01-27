package com.scrance.web.jobs;
import com.scrance.web.services.ScraperFinanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private ScraperFinanceService sraperFinanceService;

    @Scheduled(cron = "59 * * * *") // Cron expression for running every 59 minute
    public void execute() {
        sraperFinanceService.dummyMethod();
    }
}
