package com.telei.wms.schedule.task.schedule;

import com.nuochen.framework.component.task.TaskContext;
import com.nuochen.framework.component.task.TaskHandler;
import com.telei.infrastructure.component.idgenerator.IdSecondGenerator;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotDailyKnotRepository;
import com.telei.wms.datasource.wms.repository.WmsIvSnapshotTimeRepository;
import com.telei.wms.datasource.wms.repository.WmsIvTransactionDailyKnotRepository;
import com.telei.wms.datasource.wms.repository.WmsIvTransactionRepository;
import com.telei.wms.datasource.wms.vo.WmsIvSnapshotDailyKnotVO;
import com.telei.wms.datasource.wms.vo.WmsIvTransactionDailyKnotVO;
import com.telei.wms.schedule.utils.DataConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存快照日结定时任务
 *
 * @author gongrp
 */

@Slf4j
@Component
public class IvTransactionDailyKnotScheduleHandler extends TaskHandler {

    @Autowired
    private IdSecondGenerator idSecondGenerator;

    @Autowired
    private WmsIvTransactionRepository wmsIvTransactionRepository;

    @Autowired
    private WmsIvSnapshotTimeRepository wmsIvSnapshotTimeRepository;

    @Autowired
    private WmsIvSnapshotDailyKnotRepository wmsIvSnapshotDailyKnotRepository;

    @Autowired
    private WmsIvTransactionDailyKnotRepository wmsIvTransactionDailyKnotRepository;

    protected IvTransactionDailyKnotScheduleHandler() {
        super("库存变动记录日结");
    }

    @Override
    protected void handle(TaskContext context) {
        /**
         * 1.根据当前日期yyyy-mm-dd查询表wms_iv_snapshot_time符合记录的数据  期初/期末不能为空
         * 无符合条件 不执行定时任务
         * 符合条件
         * 2.过滤wms_iv_transaction表的创建时间 create_time >= 期初快照时间 and create_time < 期末快照时间
         * 3.
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        /**
         * 举例：当前时间为2020-09-11 10:00:00统计日结需要查询2020-09-10~2020-09-11的快照数据
         */
        String leftSnapshotTime = sdf.format(DateUtils.leftWithDate());
        String rightSnapshotTime = sdf.format(DateUtils.nowWithDate());
        List<WmsIvSnapshotTime> snapshotTimeList = wmsIvSnapshotTimeRepository.selectEntityByTime(leftSnapshotTime, rightSnapshotTime);
        if (StringUtils.isNotNull(snapshotTimeList) && snapshotTimeList.size() == 2) {
            Long leftIvstId = snapshotTimeList.get(0).getId();
            Long rightIvstId = snapshotTimeList.get(1).getId();
            Date leftTime = snapshotTimeList.get(0).getSnapshotTime();
            Date rightTime = snapshotTimeList.get(1).getSnapshotTime();
            String snapshotLcTime = snapshotTimeList.get(1).getSnapshotLcTime();
            /**
             * 查询wms_iv_transaction表符合条件的数据【create_time >= leftTime and create_time < rightTime】
             */
            List<WmsIvTransactionDailyKnotVO> transactionList = wmsIvTransactionRepository.selectByTime(leftTime, rightTime);
            Long ivtIdFrom = 0L;
            Long ivtIdEnd = 0L;

            Map<String, BigDecimal> leftDailyKnotVOMap = new HashMap<>();
            Map<String, BigDecimal> rightDailyKnotVOMap = new HashMap<>();
            /**
             * 根据leftIvstId rightIvstId查询出产品前一天数量 以及当天数量
             */
            List<WmsIvSnapshotDailyKnotVO> leftDailyKnotList = wmsIvSnapshotDailyKnotRepository.selectAllByIvstId(leftIvstId);
            if (StringUtils.isNotNull(leftDailyKnotList) && !leftDailyKnotList.isEmpty()) {
                leftDailyKnotVOMap = leftDailyKnotList.stream().collect(Collectors.toMap(WmsIvSnapshotDailyKnotVO::getUniqueKey, WmsIvSnapshotDailyKnotVO::getIvQty));
            }
            List<WmsIvSnapshotDailyKnotVO> rightDailyKnotList = wmsIvSnapshotDailyKnotRepository.selectAllByIvstId(rightIvstId);
            if (StringUtils.isNotNull(rightDailyKnotList) && !rightDailyKnotList.isEmpty()) {
                rightDailyKnotVOMap = rightDailyKnotList.stream().collect(Collectors.toMap(WmsIvSnapshotDailyKnotVO::getUniqueKey, WmsIvSnapshotDailyKnotVO::getIvQty));
            }
            /**
             * 根据时间查询出符合条件的id列表从中取出开始以及结束id
             */
            List<WmsIvTransaction> ivTransactionList = wmsIvTransactionRepository.selectByCreateTime(leftTime, rightTime);
            if (StringUtils.isNotNull(ivTransactionList) && !ivTransactionList.isEmpty()) {
                List<Long> ids = ivTransactionList.stream().map(WmsIvTransaction::getId).collect(Collectors.toList());
                ivtIdFrom = ids.get(0);
                ivtIdEnd = ids.get(ids.size() - 1);
            }
            List<WmsIvTransactionDailyKnot> transactionDailyKnotList = new ArrayList<>();
            if (StringUtils.isNotNull(transactionList) && !transactionList.isEmpty()) {
                long idNumber = idSecondGenerator.getUnique();
                for (WmsIvTransactionDailyKnotVO dailyKnotVO : transactionList) {
                    dailyKnotVO.setId(idNumber);
                    // 出库数量
                    BigDecimal ivtQtyOut = dailyKnotVO.getIvtQtyOut();
                    // 入库数量
                    BigDecimal ivtQtyIn = dailyKnotVO.getIvtQtyIn();
                    // 大包转换数
                    Integer bigBagQty = dailyKnotVO.getBigBagQty();
                    // 单位毛重
                    BigDecimal unitGrossWeight = dailyKnotVO.getUnitGrossWeight();
                    // 单位体积
                    BigDecimal unitVol = dailyKnotVO.getUnitVol();
                    // 出库大包数量
                    int bigBagQtyOut = ivtQtyOut.intValue() / bigBagQty;
                    // 入库大包数量
                    int bigBagQtyIn = ivtQtyIn.intValue() / bigBagQty;
                    // 出库毛重
                    BigDecimal weightOut = unitGrossWeight.multiply(ivtQtyOut);
                    // 入库毛重
                    BigDecimal weightIn = unitGrossWeight.multiply(ivtQtyIn);
                    // 出库体积
                    BigDecimal volOut = unitVol.multiply(ivtQtyOut);
                    // 入库体积
                    BigDecimal volIn = unitVol.multiply(ivtQtyIn);
                    dailyKnotVO.setBigBagQtyOut(new BigDecimal(bigBagQtyOut));
                    dailyKnotVO.setWeightOut(weightOut);
                    dailyKnotVO.setNetWeightOut(weightOut);
                    dailyKnotVO.setVolOut(volOut);
                    dailyKnotVO.setBigBagQtyIn(new BigDecimal(bigBagQtyIn));
                    dailyKnotVO.setWeightIn(weightIn);
                    dailyKnotVO.setNetWeightIn(weightIn);
                    dailyKnotVO.setVolIn(volIn);
                    dailyKnotVO.setTrayCountChange(BigDecimal.ZERO);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ParsePosition pos = new ParsePosition(0);
                    dailyKnotVO.setKnotLcDate(DateUtils.getNowDate());
                    dailyKnotVO.setKnotDate(DateUtils.nowWithUTC());
                    dailyKnotVO.setSnapshotDate(rightTime);
                    dailyKnotVO.setSnapshotLcDate(formatter.parse(snapshotLcTime, pos));
                    dailyKnotVO.setCreateTimeLc(DateUtils.getNowDate());
                    dailyKnotVO.setCreateTime(DateUtils.nowWithUTC());
                    dailyKnotVO.setIvtIdFrom(ivtIdFrom);
                    dailyKnotVO.setIvtIdEnd(ivtIdEnd);
                    String uniqueKey = dailyKnotVO.getCompanyId() + dailyKnotVO.getWarehouseId() + dailyKnotVO.getWarehouseCode() + dailyKnotVO.getCustomerId() + dailyKnotVO.getProductId();
                    dailyKnotVO.setKnotBeginQty(leftDailyKnotVOMap.get(uniqueKey));
                    dailyKnotVO.setKnotEndQty(rightDailyKnotVOMap.get(uniqueKey));
                    idNumber++;
                    WmsIvTransactionDailyKnot ivTransactionDailyKnot = DataConvertUtil.parseDataAsObject(dailyKnotVO, WmsIvTransactionDailyKnot.class);
                    transactionDailyKnotList.add(ivTransactionDailyKnot);
                }
                wmsIvTransactionDailyKnotRepository.insertBatch(transactionDailyKnotList);
            }
        }
    }

}
