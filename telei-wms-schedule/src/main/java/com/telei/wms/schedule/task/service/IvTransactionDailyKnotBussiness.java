package com.telei.wms.schedule.task.service;

import com.telei.infrastructure.component.idgenerator.IdSecondGenerator;
import com.telei.wms.commons.utils.DateUtils;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotDailyKnot;
import com.telei.wms.datasource.wms.model.WmsIvSnapshotTime;
import com.telei.wms.datasource.wms.model.WmsIvTransaction;
import com.telei.wms.datasource.wms.model.WmsIvTransactionDailyKnot;
import com.telei.wms.datasource.wms.service.WmsIvSnapshotDailyKnotService;
import com.telei.wms.datasource.wms.service.WmsIvSnapshotTimeService;
import com.telei.wms.datasource.wms.service.WmsIvTransactionDailyKnotService;
import com.telei.wms.datasource.wms.service.WmsIvTransactionService;
import com.telei.wms.datasource.wms.vo.WmsIvTransactionDailyKnotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IvTransactionDailyKnotBussiness {

    @Autowired
    private IdSecondGenerator idSecondGenerator;

    @Autowired
    private WmsIvTransactionService wmsIvTransactionService;

    @Autowired
    private WmsIvSnapshotTimeService wmsIvSnapshotTimeService;

    @Autowired
    private WmsIvSnapshotDailyKnotService wmsIvSnapshotDailyKnotService;

    @Autowired
    private WmsIvTransactionDailyKnotService wmsIvTransactionDailyKnotService;

    @Transactional(rollbackFor = Exception.class)
    public void doIvTransactionDailyKnot(){
        /**
         * 1.根据当前日期yyyy-mm-dd查询表wms_iv_snapshot_time符合记录的数据  期初/期末不能为空
         * 无符合条件 不执行定时任务
         * 符合条件
         * 2.过滤wms_iv_transaction表的创建时间 create_time >= 期初快照时间 and create_time < 期末快照时间
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        /**
         * 举例：当前时间为2020-09-11 10:00:00统计日结需要查询2020-09-10~2020-09-11的快照数据
         */
        String leftSnapshotTime = sdf.format(DateUtils.leftWithDate());
        String rightSnapshotTime = sdf.format(DateUtils.nowWithDate());
        List<WmsIvSnapshotTime> snapshotTimeList = wmsIvSnapshotTimeService.selectEntityByTime(leftSnapshotTime, rightSnapshotTime);
        if (StringUtils.isNotNull(snapshotTimeList) && snapshotTimeList.size() == 2) {
            // 获取库存快照时间表信息
            Long leftIvstId = snapshotTimeList.get(0).getId();
            Long rightIvstId = snapshotTimeList.get(1).getId();
            Date leftTime = snapshotTimeList.get(0).getSnapshotTime();
            Date rightTime = snapshotTimeList.get(1).getSnapshotTime();
            String snapshotLcTime = snapshotTimeList.get(1).getSnapshotLcTime();

            // 开始id
            Long ivtIdFrom = 0L;
            // 结束id
            Long ivtIdEnd = 0L;
            // 产品前一天数量集合
            Map<String, BigDecimal> leftDailyKnotVOMap = new HashMap<>();
            // 产品当天数量集合
            Map<String, BigDecimal> rightDailyKnotVOMap = new HashMap<>();
            // 产品库存变动集合
            Map<String, WmsIvTransactionDailyKnotVO> ivTransactionMap = new HashMap<>();

            /*
             * 根据筛选条件【create_time >= leftTime and create_time < rightTime】
             * 查询wms_iv_transaction表库存变动记录
             */
            List<WmsIvTransactionDailyKnotVO> transactionList = wmsIvTransactionService.selectByTime(leftTime, rightTime);
            if (StringUtils.isNotNull(transactionList) && !transactionList.isEmpty()) {
                ivTransactionMap = transactionList.stream().collect(Collectors.toMap(dailyKnotVO -> dailyKnotVO.getProductId().toString() + dailyKnotVO.getCompanyId().toString() + dailyKnotVO.getWarehouseId().toString(), Function.identity()));
            }

            /**
             * 根据leftIvstId查询出产品前一天库存快照日结信息
             * 根据rightIvstId查询出产品天库存快照日结信息
             */
            WmsIvSnapshotDailyKnot leftSnapshotDailyKnot = new WmsIvSnapshotDailyKnot();
            leftSnapshotDailyKnot.setIvstId(leftIvstId);
            List<WmsIvSnapshotDailyKnot> leftDailyKnotList = wmsIvSnapshotDailyKnotService.selectByEntity(leftSnapshotDailyKnot);
            if (StringUtils.isNotNull(leftDailyKnotList) && !leftDailyKnotList.isEmpty()) {
                leftDailyKnotVOMap = leftDailyKnotList.stream().collect(Collectors.toMap(dailyKnotVO -> dailyKnotVO.getProductId().toString() + dailyKnotVO.getCompanyId().toString() + dailyKnotVO.getWarehouseId().toString(), WmsIvSnapshotDailyKnot::getIvQty));
            }

            WmsIvSnapshotDailyKnot rightSnapshotDailyKnot = new WmsIvSnapshotDailyKnot();
            rightSnapshotDailyKnot.setIvstId(rightIvstId);
            List<WmsIvSnapshotDailyKnot> rightDailyKnotList = wmsIvSnapshotDailyKnotService.selectByEntity(rightSnapshotDailyKnot);
            if (StringUtils.isNotNull(rightDailyKnotList) && !rightDailyKnotList.isEmpty()) {
                rightDailyKnotVOMap = rightDailyKnotList.stream().collect(Collectors.toMap(dailyKnotVO -> dailyKnotVO.getProductId().toString() + dailyKnotVO.getCompanyId().toString() + dailyKnotVO.getWarehouseId().toString(), WmsIvSnapshotDailyKnot::getIvQty));
            }

            /**
             * 根据时间查询出符合条件的id列表从中取出开始以及结束id
             */
            List<WmsIvTransaction> ivTransactionList = wmsIvTransactionService.selectByCreateTime(leftTime, rightTime);
            if (StringUtils.isNotNull(ivTransactionList) && !ivTransactionList.isEmpty()) {
                List<Long> ids = ivTransactionList.stream().map(WmsIvTransaction::getId).collect(Collectors.toList());
                ivtIdFrom = ids.get(0);
                ivtIdEnd = ids.get(ids.size() - 1);
            }

            List<WmsIvTransactionDailyKnot> transactionDailyKnotList = new ArrayList<>();
            long idNumber = idSecondGenerator.getUnique();
            if (StringUtils.isNotNull(leftDailyKnotList) && !leftDailyKnotList.isEmpty()) {
                for (WmsIvSnapshotDailyKnot dailyKnot : leftDailyKnotList) {
                    String uniqueKey = dailyKnot.getProductId().toString() + dailyKnot.getCompanyId().toString() + dailyKnot.getWarehouseId().toString();
                    WmsIvTransactionDailyKnot wmsIvSnapshotDailyKnot = new WmsIvTransactionDailyKnot();
                    wmsIvSnapshotDailyKnot.setId(idNumber);
                    wmsIvSnapshotDailyKnot.setProductId(dailyKnot.getProductId());
                    wmsIvSnapshotDailyKnot.setCompanyId(dailyKnot.getCompanyId());
                    wmsIvSnapshotDailyKnot.setWarehouseId(dailyKnot.getWarehouseId());
                    wmsIvSnapshotDailyKnot.setWarehouseCode(dailyKnot.getWarehouseCode());
                    wmsIvSnapshotDailyKnot.setCustomerId(dailyKnot.getCustomerId());

                    // 出库数量
                    BigDecimal ivtQtyOut;
                    // 入库数量
                    BigDecimal ivtQtyIn;
                    // 大包转换数
                    Integer bigBagQty;
                    // 单位毛重
                    BigDecimal unitGrossWeight;
                    // 单位毛重
                    BigDecimal unitVol;
                    // 出库大包数量
                    int bigBagQtyOut = 0;
                    // 入库大包数量
                    int bigBagQtyIn = 0;
                    // 出库毛重
                    BigDecimal weightOut = BigDecimal.ZERO;
                    // 入库毛重
                    BigDecimal weightIn = BigDecimal.ZERO;
                    // 出库体积
                    BigDecimal volOut = BigDecimal.ZERO;
                    // 入库体积
                    BigDecimal volIn = BigDecimal.ZERO;
                    WmsIvTransactionDailyKnotVO knotVO = ivTransactionMap.get(uniqueKey);
                    if (Objects.nonNull(knotVO)) {
                        ivtQtyOut = knotVO.getIvtQtyOut();
                        ivtQtyIn = knotVO.getIvtQtyIn();
                        bigBagQty = knotVO.getBigBagQty();
                        unitGrossWeight = knotVO.getUnitGrossWeight();
                        unitVol = knotVO.getUnitVol();
                        bigBagQtyOut = ivtQtyOut.intValue() / bigBagQty;
                        bigBagQtyIn = ivtQtyIn.intValue() / bigBagQty;
                        weightOut = unitGrossWeight.multiply(ivtQtyOut);
                        weightIn = unitGrossWeight.multiply(ivtQtyIn);
                        volOut = unitVol.multiply(ivtQtyOut);
                        volIn = unitVol.multiply(ivtQtyIn);
                    }
                    wmsIvSnapshotDailyKnot.setBigBagQtyOut(new BigDecimal(bigBagQtyOut));
                    wmsIvSnapshotDailyKnot.setWeightOut(weightOut);
                    wmsIvSnapshotDailyKnot.setNetWeightOut(weightOut);
                    wmsIvSnapshotDailyKnot.setVolOut(volOut);
                    wmsIvSnapshotDailyKnot.setBigBagQtyIn(new BigDecimal(bigBagQtyIn));
                    wmsIvSnapshotDailyKnot.setWeightIn(weightIn);
                    wmsIvSnapshotDailyKnot.setNetWeightIn(weightIn);
                    wmsIvSnapshotDailyKnot.setVolIn(volIn);
                    wmsIvSnapshotDailyKnot.setTrayCountChange(BigDecimal.ZERO);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ParsePosition pos = new ParsePosition(0);
                    wmsIvSnapshotDailyKnot.setKnotLcDate(DateUtils.getNowDate());
                    wmsIvSnapshotDailyKnot.setKnotDate(DateUtils.nowWithUTC());
                    wmsIvSnapshotDailyKnot.setSnapshotDate(rightTime);
                    wmsIvSnapshotDailyKnot.setSnapshotLcDate(formatter.parse(snapshotLcTime, pos));
                    wmsIvSnapshotDailyKnot.setCreateTimeLc(DateUtils.getNowDate());
                    wmsIvSnapshotDailyKnot.setCreateTime(DateUtils.nowWithUTC());
                    wmsIvSnapshotDailyKnot.setIvtIdFrom(ivtIdFrom);
                    wmsIvSnapshotDailyKnot.setIvtIdEnd(ivtIdEnd);
                    wmsIvSnapshotDailyKnot.setKnotBeginQty(leftDailyKnotVOMap.get(uniqueKey));
                    wmsIvSnapshotDailyKnot.setKnotEndQty(rightDailyKnotVOMap.get(uniqueKey));
                    transactionDailyKnotList.add(wmsIvSnapshotDailyKnot);

                    idNumber++;
                }
            }
            if (transactionDailyKnotList.size() > 0) {
                wmsIvTransactionDailyKnotService.insertBatch(transactionDailyKnotList);
            }
        }
    }

}
