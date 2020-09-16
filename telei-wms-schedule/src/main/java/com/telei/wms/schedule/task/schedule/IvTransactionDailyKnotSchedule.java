package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskCron;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gongrp
 * 库存变动记录日结
 */
@Slf4j
@Component
public class IvTransactionDailyKnotSchedule {

    @Autowired
    private IvTransactionDailyKnotScheduleHandler ivTransactionDailyKnotScheduleHandler;

    @Scheduled(cron = TaskCron.EVERY_DAY_6)
    public void doIvTransactionDailyKnotSchedule(){
        ivTransactionDailyKnotScheduleHandler.run();
    }

}
