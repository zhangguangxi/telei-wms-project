package com.telei.wms.schedule.task.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gongrp
 * 库存快照日结统计任务
 */
@Slf4j
@Component
public class IvSnapshotDailyKnotSchedule {

    @Autowired
    private IvSnapshotDailyKnotScheduleHandler ivSnapshotDailyKnotScheduleHandler;

    @Scheduled(cron = "0 10 1 * * ?")
    public void doIvSnapshotDailyKnotSchedule(){
        ivSnapshotDailyKnotScheduleHandler.run();
    }

}
