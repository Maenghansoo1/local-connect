package com.local.connect.scheduler;

import com.local.connect.event.EventService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class EventSyncJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(EventSyncJob.class);

    @Autowired
    private EventService eventService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("배치 시작 - 공공API 데이터 동기화");
        eventService.syncAll();
        log.info("배치 완료");
    }
}
