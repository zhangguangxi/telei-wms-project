package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.commons.utils.PageCommonRequest;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/10 18:35
 */
@Data
public class InventoryAdjustPageQueryBussinessRequest {
    private PageCommonRequest pageCommonRequest;
    private InventoryPageQueryCondition inventoryPageQueryCondition;

    @Data
    public static class InventoryPageQueryCondition {
        /**库位*/
        private String lcCode;
        /**商品码*/
        private String productNo;
        /**商品名称*/
        private String productName;
        /**商品条码*/
        private String productBarcode;
        /**库位类型*/
        private String lcType;
        /**调整类型*/
        private String adjhType;
    };
}
