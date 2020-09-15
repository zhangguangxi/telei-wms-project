package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskCron;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gongrp
 * 库存快照统计任务
 */
@Slf4j
@Component
public class IvSnapshotSchedule {

    @Autowired
    private IvSnapshotScheduleHandler ivSnapshotScheduleHandler;

    @Scheduled(cron = TaskCron.EVERY_SECONDS_1)
    public void doIvSnapshotSchedule(){
        ivSnapshotScheduleHandler.run();
    }

}
