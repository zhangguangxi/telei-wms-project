package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/10 17:49
 */
@Data
public class InventoryDetailBussinessResponse {
    private List<InventoryDetailResponse.InventoryDetailCondition> list;

    @Data
    public static  class InventoryDetailCondition{
        /**库存ID*/
        private Long ivId;

        /**库存批次*/
        private Long iabId;

        /**库存数*/
        private BigDecimal ivQty;

        /**打包数*/
        private  BigDecimal bigBagQty;

        /**中包数(库存数/大包转化率的余数)*/
        private  BigDecimal midBagQty;

        /**小包数(库存数/中包转换率的余数)*/
        private  BigDecimal tinyBagQty;
    }
}
