package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.wms.schedule.task.service.IvTransactionDailyKnotBussiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 库存快照日结定时任务
 *
 * @author gongrp
 */

@Slf4j
@Component
public class IvTransactionDailyKnotScheduleHandler extends TaskHandler {

    @Autowired
    private IvTransactionDailyKnotBussiness ivTransactionDailyKnotBussiness;

    protected IvTransactionDailyKnotScheduleHandler() {
        super("库存变动记录日结");
    }

    @Override
    protected void handle(TaskContext context) {
        ivTransactionDailyKnotBussiness.doIvTransactionDailyKnot();
    }

}
