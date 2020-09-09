package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskCron;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gongrp
 */
@Slf4j
@Component
public class IvSnapshotDailyKnotSchedule {

    @Autowired
    private IvSnapshotDailyKnotScheduleHandler ivSnapshotDailyKnotScheduleHandler;

//    @Scheduled(cron = TaskCron.EVERY_DAY_4)
    @Scheduled(cron = TaskCron.EVERY_SECONDS_15)
    public void doIvSnapshotDailyKnotSchedule(){
        ivSnapshotDailyKnotScheduleHandler.run();
    }

}
