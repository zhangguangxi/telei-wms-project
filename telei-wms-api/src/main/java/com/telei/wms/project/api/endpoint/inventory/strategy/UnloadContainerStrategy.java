package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import com.telei.wms.datasource.wms.model.WmsPloLine;
import com.telei.wms.datasource.wms.vo.WmsInventoryDeductConditionVo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 库存扣减-装柜
 *
 * @author: leo
 * @date: 2020/9/8 16:29
 */
@Component("unloadContainerStrategy")
public class UnloadContainerStrategy implements IDeductStrategy {
    @Override
    public List<WmsInventoryDeductConditionVo> process(List<Long> requestProductList, List<String> requestLcCodeList, Map<String, BigDecimal> productIdAndLcCode2RealQty, List<WmsIvOut> wmsIvOutList, List<WmsDoLine> updateDoLineList,
                                                       Map<Long, WmsDoLine> id2DoLineEntityMap, Map<Long, WmsPloLine> id2PloLineEntityMap,
                                                       Map<Long, String> dolId2LcCodeMap, List<Long> deleteIvOutList,
                                                       Long dohId, Long warehouseId, Long companyId) {

        List<WmsInventoryDeductConditionVo> inventoryDeductConditionList = new ArrayList<>();
        wmsIvOutList.stream().filter(item -> requestProductList.contains(item.getProductId())).forEach(item -> {
            Long dolId = item.getLineId();//出库任务明细id
            String lcCode = dolId2LcCodeMap.get(dolId);
            if (requestLcCodeList.contains(lcCode)) {
                deleteIvOutList.add(dolId);
                WmsDoLine doLine = id2DoLineEntityMap.get(dolId);
                WmsPloLine ploLine = id2PloLineEntityMap.get(dolId);
                doLine.setShipQty(ploLine.getPickedQty());//实际出库数量-拣货数量
                doLine.setShipVol(ploLine.getPickedVol());//实际出库体积-拣货体积
                doLine.setShipWeight(ploLine.getWeight());//实际出库重量-拣货重量
                updateDoLineList.add(doLine);

                WmsInventoryDeductConditionVo deductCondition = new WmsInventoryDeductConditionVo();
                Long productId = item.getProductId();
                deductCondition.setCompanyId(item.getCompanyId());//公司id
                deductCondition.setWarehouseId(item.getWarehouseId());//仓库id
                deductCondition.setProductId(productId);//产品id
                deductCondition.setIvoId(item.getIvoId());//待出库存id
                deductCondition.setLineId(dolId);//出库单明细id
                deductCondition.setQty(item.getQty());//计划扣减-数量
                deductCondition.setRealQty(productIdAndLcCode2RealQty.get(productId + "#" + lcCode));//实际扣减数量
                deductCondition.setLcCode(lcCode);//库位
                deductCondition.setSpId(doLine.getSpId());//出库计划id
                deductCondition.setSpdId(doLine.getSpdId());//出库计划明细id
                deductCondition.setSoId(doLine.getSoId());//销售单id
                deductCondition.setSodId(doLine.getSodId());//销售单详情id
                inventoryDeductConditionList.add(deductCondition);
            }
        });
        return inventoryDeductConditionList;
    }
}