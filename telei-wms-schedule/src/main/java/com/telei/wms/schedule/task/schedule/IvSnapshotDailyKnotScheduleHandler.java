package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.infrastructure.component.idgenerator.IdSecondGenerator;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.datasource.wms.model.WmsIvSnapshot;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotDailyKnot;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotDailyKnotRepository;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotRepository;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotTimeRepository;
import com.telei.wms.datasource.wms.vo.WmsIvSnapshotDailyKnotVO;
import com.telei.wms.schedule.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 库存快照日结定时任务
 *
 * @author gongrp
 */

@Slf4j
@Component
public class IvSnapshotDailyKnotScheduleHandler extends TaskHandler {

    @Autowired
    private IdSecondGenerator idSecondGenerator;

    @Autowired
    private WmsIvSnapshotTimeRepository wmsIvSnapshotTimeRepository;

    @Autowired
    private WmsIvSnapshotRepository wmsIvSnapshotRepository;

    @Autowired
    private WmsIvSnapshotDailyKnotRepository wmsIvSnapshotDailyKnotRepository;

    protected IvSnapshotDailyKnotScheduleHandler() {
        super("库存快照日结");
    }

    @Override
    protected void handle(TaskContext context) {
        /**
         * wms_iv_snapshot_daily_knot 取max(ivsdk_id)
         * 	有值
         * 		snapshot_date比对wms_iv_snapshot_time表snapshot_time
         * 	无值：
         * 		取wms_iv_snapshot_time表snapshot_time倒序最新的一条数据
         */
        // 获取库存快照日结算表主键id最大的一条数据
        WmsIvSnapshotDailyKnot wmsIvSnapshotDailyKnot = wmsIvSnapshotDailyKnotRepository.selectMaxId();
        // 库存快照时间表最新的一条记录
        WmsIvSnapshotTime wmsIvSnapshotTime = wmsIvSnapshotTimeRepository.selectNewEntity();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        // 是否存在日结数据
        boolean isExist = false;
        // 当前库存快照时间是否执行过
        boolean staticTimeExist = true;
        if (Objects.nonNull(wmsIvSnapshotDailyKnot)) {
            // 比较
            if (Objects.nonNull(wmsIvSnapshotTime)) {
                String snapshotDailyTime = sd.format(wmsIvSnapshotDailyKnot.getSnapshotDate());
                String snapshotTime = sd.format(wmsIvSnapshotTime.getSnapshotTime());
                if (snapshotTime.equals(snapshotDailyTime)) {
                    isExist = true;
                } else {
                    staticTimeExist = false;
                }
            } else {
                // 库存快照时间表无数据【日结表无法统计】
                isExist = true;
            }
        }
        if (!isExist) {
            // 查询库存快照表【wms_iv_snapshot】
            List<WmsIvSnapshotDailyKnotVO> dailyKnotVOList = wmsIvSnapshotRepository.selectByStatistics(wmsIvSnapshotTime.getId());
            if (StringUtils.isNotNull(dailyKnotVOList) && !dailyKnotVOList.isEmpty()) {
                List<WmsIvSnapshotDailyKnot> snapshotDailyKnotList = new ArrayList<>();
                Long idNumber = idSecondGenerator.getUnique();
                for (WmsIvSnapshotDailyKnotVO dailyKnotVO : dailyKnotVOList) {
                    WmsIvSnapshotDailyKnot snapshotDailyKnot = DataConvertUtil.parseDataAsObject(dailyKnotVO, WmsIvSnapshotDailyKnot.class);
                    snapshotDailyKnot.setId(idNumber);
                    snapshotDailyKnot.setCreateTime(DateUtils.nowWithUTC());
                    try {
                        snapshotDailyKnot.setKnotLcDate(sd.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                        snapshotDailyKnot.setKnotDate(DateUtils.nowWithUTC());
                        if (!staticTimeExist) {
                            snapshotDailyKnot.setSnapshotDate(wmsIvSnapshotTime.getSnapshotTime());
                            snapshotDailyKnot.setSnapshotLcDate(sd.parse(wmsIvSnapshotTime.getSnapshotLcTime()));
                        } else {
                            snapshotDailyKnot.setSnapshotDate(DateUtils.nowWithUTC());
                            snapshotDailyKnot.setSnapshotLcDate(sd.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    snapshotDailyKnotList.add(snapshotDailyKnot);
                    idNumber++;
                }
                wmsIvSnapshotDailyKnotRepository.insertBatch(snapshotDailyKnotList);
            }
        }
    }

}
