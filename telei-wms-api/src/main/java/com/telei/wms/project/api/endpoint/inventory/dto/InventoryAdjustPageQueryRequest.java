package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.commons.utils.PageCommonRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/10 18:31
 */
@Data
public class InventoryAdjustPageQueryRequest {
    private PageCommonRequest pageCommonRequest;
    private InventoryAdjustPageQueryCondition inventoryPageQueryCondition;

    @Data
    public static class InventoryAdjustPageQueryCondition {
        @ApiModelProperty(value = "库位",example = "s11-12-13",position = 1)
        private String lcCode;

        @ApiModelProperty(value = "商品码",example = "113143214",position = 2)
        private String productNo;

        @ApiModelProperty(value = "商品名称",example = "篮球",position = 3)
        private String productName;

        @ApiModelProperty(value = "商品条码",example = "12345678",position = 4)
        private String productBarcode;

        @ApiModelProperty(value = "库位类型",example = "库位类型，数据字典，S 样品库位、E 高架库位 等",position = 5)
        private String lcType;

        @ApiModelProperty(value = "调整类型",example = "调整类型，MOVE-移位，INCREASE-调增，REDUCE-调减，LIFTUP-升任务，LIFTDOWN-降任务")
        private String adjhType;
    }
}
