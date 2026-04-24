package com.local.connect.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail eventSyncJobDetail() {
        return JobBuilder.newJob(EventSyncJob.class)
                .withIdentity("eventSyncJob")
                .storeDurably()
                .build();
    }

    // 매일 새벽 2시 자동 동기화
    @Bean
    public Trigger eventSyncTrigger(JobDetail eventSyncJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(eventSyncJobDetail)
                .withIdentity("eventSyncTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))
                .build();
    }
}
