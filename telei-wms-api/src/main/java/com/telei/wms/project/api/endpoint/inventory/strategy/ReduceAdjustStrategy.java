package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.google.common.collect.Lists;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import com.telei.infrastructure.component.commons.utils.CollectorsUtil;
import com.telei.wms.customer.amqp.inventoryChangeWriteBack.OmsInventoryChangeWriteBack;
import com.telei.wms.datasource.wms.model.*;
import com.telei.wms.datasource.wms.service.WmsInventoryService;
import com.telei.wms.datasource.wms.service.WmsIvOutService;
import com.telei.wms.project.api.ErrorCode;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author: leo
 * @date: 2020/9/8 16:30
 */
@Component("reduceAdjustStrategy")
public class ReduceAdjustStrategy implements IAdjustStrategy{
    @Autowired
    private AdjustStrategyFactory adjustStrategyFactory;

    @Autowired
    private WmsIvOutService wmsIvOutService;

    @Autowired
    private WmsInventoryService wmsInventoryService;


    @Override
    public List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> process(WmsAdjtHeader wmsAdjtHeader, List<WmsAdjtLine> wmsAdjtLineList, List<WmsInventory> WmsInventoryDbList,
                                                                                    List<WmsInventory> wmsInventoryAddList, List<WmsInventory> wmsInventoryUpdateList, List<Long> deleteIvidList,
                                                                                    List<WmsIvTransaction> wmsIvTransactionList,
                                                                                    List<WmsIvSplit> wmsIvSplitList,UserInfo userInfo, Date nowWithUtc) {
        WmsInventory wmsInventory = WmsInventoryDbList.get(0);
        BigDecimal ivQtyAdjt = wmsAdjtHeader.getIvQtyAdjt();/**库存调整数*/
        String lcCode = wmsAdjtHeader.getLcCode(); /**库位编码*/

        wmsAdjtHeader.setLcCodeAdjt(lcCode);/**调整库位*/
        wmsAdjtHeader.setBigBagRate(wmsInventory.getBigBagRate());/**大包转换率*/
        wmsAdjtHeader.setMidBagRate(wmsInventory.getMidBagRate());/**中包转换率*/

        List<WmsInventory> wmsInventories = DataConvertUtil.parseDataAsArray(WmsInventoryDbList, WmsInventory.class);
        BigDecimal totalIvQtyForLcCode = wmsInventories.stream().map(WmsInventory::getIvQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<WmsIvOut> wmsIvOuts = wmsIvOutService.selectAll();

        WmsInventory inventory = wmsInventories.get(0);
        Long companyId = inventory.getCompanyId();
        Long warehouseId = inventory.getWarehouseId();
        Long productId = inventory.getProductId();
        WmsInventory inventoryCondition = new WmsInventory();
        inventoryCondition.setCompanyId(companyId);
        inventoryCondition.setWarehouseId(warehouseId);
        inventoryCondition.setProductId(productId);
        List<WmsInventory> inventories = wmsInventoryService.selectByEntity(inventoryCondition);
        BigDecimal totalIvQtyForProduct = inventories.stream().collect(CollectorsUtil.summingBigDecimal(WmsInventory::getIvQty));
        if(Objects.nonNull(wmsIvOuts) && !wmsIvOuts.isEmpty()){
            Map<String, BigDecimal> productRecord2QtyMap = wmsIvOuts.stream().collect(Collectors.groupingBy((item -> item.getCompanyId() + "#" + item.getWarehouseId() + "#" + item.getProductId()), CollectorsUtil.summingBigDecimal(WmsIvOut::getQty)));
            BigDecimal productIvOutQty = productRecord2QtyMap.get(companyId + "#" + warehouseId + "#" + productId);
            if(Objects.nonNull(productIvOutQty)){
                totalIvQtyForProduct = totalIvQtyForProduct.subtract(productIvOutQty);
            }
        }
        if(totalIvQtyForProduct.compareTo(ivQtyAdjt) < 0){
            ErrorCode.ADJT_ERROR_4018.throwError(wmsAdjtHeader.getCompanyId(),wmsAdjtHeader.getWarehouseId(),wmsAdjtHeader.getProductId(),ivQtyAdjt,totalIvQtyForLcCode,wmsAdjtHeader.getAdjhType());
        }
        int compareRet = totalIvQtyForLcCode.compareTo(ivQtyAdjt);
        if(compareRet < 0){
            ErrorCode.ADJT_ERROR_4013.throwError(wmsAdjtHeader.getLcCode(),wmsAdjtHeader.getProductId(),ivQtyAdjt,totalIvQtyForLcCode,wmsAdjtHeader.getAdjhType());
        }
        List<OmsInventoryChangeWriteBack.OmsInventoryChangeWriteBackCondition> list = Lists.newArrayList();
        for (WmsInventory item : wmsInventories) {
            BigDecimal ivQty = item.getIvQty();
            if(ivQty.compareTo(ivQtyAdjt) > 0){
                /**当前批次库存数 > 调整库存数*/
                item.setIvQty(ivQty.subtract(ivQtyAdjt));
                item.setIvTranstime(nowWithUtc);
                /**库存记录更新*/
                wmsInventoryUpdateList.add(item);
                /**库存变更记录新增*/
                adjustStrategyFactory.createTransactionRecored(wmsIvTransactionList,item,lcCode,"LESS",ivQtyAdjt,userInfo, nowWithUtc);
                /***oms库存记回写录*/
                adjustStrategyFactory.createOmsInventoryChangeWriteBackCondition(ivQtyAdjt, list, item,2,wmsAdjtHeader);
                /***库存调整单明细记录*/
                adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, item,null,"REDUCE" ,ivQtyAdjt, lcCode);
                break;
            }

            deleteIvidList.add(item.getIvId());
            /**库存变更记录*/
            adjustStrategyFactory.createTransactionRecored(wmsIvTransactionList,item,lcCode,"LESS",ivQtyAdjt,userInfo, nowWithUtc);
            /**OMS库存回写记录*/
            adjustStrategyFactory.createOmsInventoryChangeWriteBackCondition(ivQtyAdjt, list, item,2,wmsAdjtHeader);
            /***库存调整单明细记录*/
            adjustStrategyFactory.createAdjtLine(wmsAdjtHeader, wmsAdjtLineList, item,null,"REDUCE" ,ivQtyAdjt, lcCode);

            if(ivQty.compareTo(ivQtyAdjt) == 0){
                break;
            }

            /**当前批次库存数 < 调整库存数*/
            ivQtyAdjt = ivQtyAdjt.subtract(ivQty);
        }
        return list;
    }
}
