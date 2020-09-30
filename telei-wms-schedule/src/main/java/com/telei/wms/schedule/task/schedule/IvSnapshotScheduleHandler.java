package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.infrastructure.component.idgenerator.IdSecondGenerator;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.datasource.wms.repository.WmsInventoryRepository;
import com.telei.wms.schedule.configuration.IdInstantdirectiveConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 库存快照定时任务
 * @author gongrp
 */

@Slf4j
@Component
public class IvSnapshotScheduleHandler extends TaskHandler {

    @Autowired
    private WmsInventoryRepository wmsInventoryRepository;

    @Autowired
    private IdSecondGenerator idSecondGenerator;

    @Autowired
    private IdInstantdirectiveConfig idInstantdirectiveConfig;

    protected IvSnapshotScheduleHandler() {
        super("库存快照");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void handle(TaskContext context) {
        Long idNumber = idSecondGenerator.getUnique();
        String serverNo = idInstantdirectiveConfig.getServerName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String snapshotTime = sdf.format(DateUtils.nowWithUTC());
        String snapshotLcTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        wmsInventoryRepository.doIvSnapshotSchedule(idNumber, serverNo, snapshotTime, snapshotLcTime);
        throw new NullPointerException();
    }
}
