package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/10 17:11
 */
@Data
public class InventoryDetailResponse {
    private List<InventoryDetailResponse.InventoryDetailCondition> list;

    @Data
    public static  class InventoryDetailCondition{
        /**批次*/
        private Long iabId;
        /**数量*/
        private BigDecimal qty;
        /**大包数量*/
        private BigDecimal bigBagQty;
        /**中包数量*/
        private BigDecimal midBagQty;
        /**小包数量*/
        private BigDecimal tinyBagQty;
    }
}

