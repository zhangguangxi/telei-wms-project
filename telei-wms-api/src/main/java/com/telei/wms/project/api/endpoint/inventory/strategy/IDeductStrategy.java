package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.telei.wms.datasource.wms.model.WmsDoLine;
import com.telei.wms.datasource.wms.model.WmsIvOut;
import com.telei.wms.datasource.wms.model.WmsPloLine;
import com.telei.wms.datasource.wms.vo.WmsInventoryDeductConditionVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author: leo
 * @date: 2020/9/8 15:47
 */
public interface IDeductStrategy {
    default List<WmsInventoryDeductConditionVo> process(List<Long> requestProductList, List<String> requestLcCodeList, Map<String, BigDecimal> productIdAndLcCode2RealQty, List<WmsIvOut> wmsIvOutList, List<WmsDoLine> updateDoLineList,
                                                        Map<Long, WmsDoLine> id2DoLineEntityMap, Map<Long, WmsPloLine> id2PloLineEntityMap,
                                                        Map<Long, String> dolId2LcCodeMap,
                                                        Long dohId, Long warehouseId, Long companyId
    ) {

        return null;
    }

    ;
}
