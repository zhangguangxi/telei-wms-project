package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.wms.schedule.task.service.IvSnapshotDailyKnotBussiness;
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
public class IvSnapshotDailyKnotScheduleHandler extends TaskHandler {

    @Autowired
    private IvSnapshotDailyKnotBussiness ivSnapshotDailyKnotBussiness;

    protected IvSnapshotDailyKnotScheduleHandler() {
        super("库存快照日结");
    }

    @Override
    protected void handle(TaskContext context) {
        ivSnapshotDailyKnotBussiness.doIvSnapshotDailKnot();
    }

}
