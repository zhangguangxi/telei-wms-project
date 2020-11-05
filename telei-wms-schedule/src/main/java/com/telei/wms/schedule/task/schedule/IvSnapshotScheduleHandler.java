package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.wms.schedule.task.service.IvSnapshotBussiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 库存快照定时任务
 *
 * @author gongrp
 */

@Slf4j
@Component
public class IvSnapshotScheduleHandler extends TaskHandler {

    @Autowired
    private IvSnapshotBussiness ivSnapshotBussiness;

    protected IvSnapshotScheduleHandler() {
        super("库存快照");
    }

    @Override
    protected void handle(TaskContext context) {
        ivSnapshotBussiness.doIvSnapshot();
    }

}
