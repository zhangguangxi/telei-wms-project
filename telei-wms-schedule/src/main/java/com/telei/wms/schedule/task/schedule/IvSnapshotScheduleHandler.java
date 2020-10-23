package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.infrastructure.component.idgenerator.IdSecondGenerator;
import com.telei.wms.commons.amqp.entity.OrderContext;
import com.telei.wms.commons.amqp.entity.ParamType;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.datasource.wms.model.SnapshotMaxId;
import com.telei.wms.datasource.wms.model.WmsIvSnapshot;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import com.telei.wms.datasource.wms.repository.WmsInventoryRepository;
import com.telei.wms.datasource.wms.service.SnapshotMaxIdService;
import com.telei.wms.datasource.wms.service.WmsIvSnapshotService;
import com.telei.wms.datasource.wms.service.WmsIvSnapshotTimeService;
import com.telei.wms.schedule.configuration.IdInstantdirectiveConfig;
import com.telei.wms.schedule.idInstantdirective.IdInstantdirectiveBussiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 库存快照定时任务
 *
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

    @Autowired
    private IdInstantdirectiveBussiness idInstantdirectiveBussiness;

    @Autowired
    private SnapshotMaxIdService snapshotMaxIdService;

    @Autowired
    private WmsIvSnapshotService wmsIvSnapshotService;

    @Autowired
    private WmsIvSnapshotTimeService wmsIvSnapshotTimeService;

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
        // 写入库存快照指令表
        ParamType snapParamType = new ParamType();
        snapParamType.setType("List");
        snapParamType.setGenericType("WmsIvSnapshot");
        List<ParamType> snapParamTypeList = new ArrayList<>();
        snapParamTypeList.add(snapParamType);
        List<WmsIvSnapshot> snapshotList;
        Long spMaxId = 0L;
        Long spId = 0L;
        do {
            List<SnapshotMaxId> maxIdList = snapshotMaxIdService.selectAll();
            if (StringUtils.isNotNull(maxIdList) && !maxIdList.isEmpty()) {
                spId = maxIdList.get(0).getId();
                spMaxId = maxIdList.get(0).getSpMaxId();
            }
            snapshotList = wmsIvSnapshotService.findAll(idNumber + 1, spMaxId);
            if (StringUtils.isNotNull(snapshotList) && !snapshotList.isEmpty()) {
                spMaxId = snapshotList.get(snapshotList.size() - 1).getId();
            }
            SnapshotMaxId snapshotMaxId = new SnapshotMaxId();
            if (spId > 0) {
                snapshotMaxId.setId(spId);
                snapshotMaxId.setSpMaxId(spMaxId);
                snapshotMaxIdService.updateByPrimaryKeySelective(snapshotMaxId);
            } else {
                snapshotMaxId.setId(idSecondGenerator.getUnique());
                snapshotMaxId.setSpMaxId(spMaxId);
                snapshotMaxIdService.insert(snapshotMaxId);
            }
        } while (snapshotList.size() > 0);
        OrderContext snapOrderContext = new OrderContext();
        snapOrderContext.setClassName("WmsIvSnapshotService");
        snapOrderContext.setMethodName("insertBatch");
        snapOrderContext.setParamTypes(snapParamTypeList);
        snapOrderContext.setBody(new Object[]{snapshotList});
        idInstantdirectiveBussiness.add("wms_iv_snapshot", "ADD", snapOrderContext);
        // 写入库存快照时间指令表
        ParamType paramType = new ParamType();
        paramType.setType("List");
        paramType.setGenericType("WmsIvSnapshotTime");
        List<ParamType> paramTypeList = new ArrayList<>();
        paramTypeList.add(paramType);
        List<Long> ids = new ArrayList<>();
        ids.add(idNumber + 1);
        List<WmsIvSnapshotTime> snapshotTimeList = wmsIvSnapshotTimeService.selectByPrimaryKeys(ids);
        OrderContext orderContext = new OrderContext();
        orderContext.setClassName("WmsIvSnapshotTimeService");
        orderContext.setMethodName("insertBatch");
        orderContext.setParamTypes(paramTypeList);
        orderContext.setBody(new Object[]{snapshotTimeList});
        idInstantdirectiveBussiness.add("wms_iv_snapshot_time", "ADD", orderContext);
    }

}
