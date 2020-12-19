package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import com.telei.wms.datasource.wms.model.WmsPloLine;
import com.telei.wms.datasource.wms.service.WmsDoContainerService;
import com.telei.wms.datasource.wms.vo.DoContainerGroupResponseVo;
import com.telei.wms.datasource.wms.vo.WmsInventoryDeductConditionVo;
import com.telei.wms.project.api.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 库存扣减-装柜
 *
 * @author: leo
 * @date: 2020/9/8 16:29
 */
@Component("loadContainerStrategy")
public class LoadContainerStrategy implements IDeductStrategy {
    @Autowired
    private WmsDoContainerService wmsDoContainerService;

    @Override
    public List<WmsInventoryDeductConditionVo> process(List<Long> requestProductList, List<String> requestLcCodeList, Map<String, BigDecimal> productIdAndLcCode2RealQty, List<WmsIvOut> wmsIvOutList, List<WmsDoLine> updateDoLineList,
                                                       Map<Long, WmsDoLine> id2DoLineEntityMap, Map<Long, WmsPloLine> id2PloLineEntityMap,
                                                       Map<Long, String> dolId2LcCodeMap,
                                                       Long dohId, Long warehouseId, Long companyId) {
        List<DoContainerGroupResponseVo> wmsDoContainers = wmsDoContainerService.selectByDohId(dohId);
        if (Objects.isNull(wmsDoContainers) || wmsDoContainers.isEmpty()) {
            ErrorCode.INVENTORY_DEDUCT_CONTAINER_IS_NULL_40056.throwError();
        }
        List<WmsInventoryDeductConditionVo> inventoryDeductConditionList = new ArrayList<>();
        Map<Long, WmsIvOut> lineId2IvOutEntityMap = wmsIvOutList.stream().collect(Collectors.toMap(WmsIvOut::getLineId, Function.identity()));
        wmsDoContainers.forEach(item -> {
            Long dolId = item.getDolId();//出库任务明细id
            WmsDoLine doLine = id2DoLineEntityMap.get(dolId);
            doLine.setShipQty(item.getCQty());//实际出库数量-装柜数量
            doLine.setShipVol(item.getCVol());//实际出库体积-装柜体积
            doLine.setShipWeight(item.getCWeight());//实际出库重量-装柜重量

            doLine.setShipBigBagQty(item.getBigBagQty()); //实际出库-大包数
            doLine.setShipMidBagQty(item.getMidBagQty());//实际出库-中包数
            doLine.setShipSmallBagQty(item.getSmallBagQty());//实际出库-小包数

            updateDoLineList.add(doLine);
            BigDecimal realQty = item.getCQty();
            String lcCode = dolId2LcCodeMap.get(dolId);
            WmsInventoryDeductConditionVo deductCondition = new WmsInventoryDeductConditionVo();
            WmsIvOut ivout = lineId2IvOutEntityMap.get(dolId);
            Long productId = item.getProductId();
            deductCondition.setProductId(productId);
            deductCondition.setCompanyId(companyId);
            deductCondition.setWarehouseId(warehouseId);
            deductCondition.setIvoId(ivout.getIvoId());//带出库存id
            deductCondition.setLineId(dolId);
            deductCondition.setQty(ivout.getQty());//计划扣减-数量
            deductCondition.setRealQty(realQty);//实际扣减数量
            deductCondition.setLcCode(lcCode);//库位
            deductCondition.setSpId(doLine.getSpId());//出库计划id
            deductCondition.setSpdId(doLine.getSpdId());//出库计划明细id
            deductCondition.setSoId(doLine.getSoId());//销售单id
            deductCondition.setSodId(doLine.getSodId());//销售单详情id
            inventoryDeductConditionList.add(deductCondition);
        });
        return inventoryDeductConditionList;
    }
}