package com.telei.wms.project.api.endpoint.inventory.dto;

import com.telei.wms.commons.utils.PageCommonRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/8/26 10:18
 */
@Data
public class InventoryPageQueryRequest {
    private PageCommonRequest pageCommonRequest;
    private InventoryPageQueryCondition inventoryPageQueryCondition;

    @Data
    public static class InventoryPageQueryCondition {
        @ApiModelProperty(value = "仓库ID",example = "123412341",position = 0)
        private String warehouseId;

        @ApiModelProperty(value = "库位",example = "s11-12-13",position = 1)
        private String lcCode;

        @ApiModelProperty(value = "商品码",example = "113143214",position = 2)
        private String productNo;

        @ApiModelProperty(value = "商品名称",example = "篮球",position = 3)
        private String productName;

        @ApiModelProperty(value = "商品条码",example = "12345678",position = 4)
        private String productBarcode;

        @ApiModelProperty(value = "库位类型",example = "S",position = 5)
        private String lcType;

        @ApiModelProperty(value = "是否有货 全部：不传 是：Y 否：N",example = "Y",position = 6)
        private String hasLcType;
    }
}
