package com.telei.wms.project.api.endpoint.inventory.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/9/10 17:11
 */
@Data
public class InventoryDetailResponse {

    private List<InventoryDetailCondition> list;

    @Data
    public static  class InventoryDetailCondition{
        @ApiModelProperty(value = "库存ID",example = "12341234",position = 0)
        private Long ivId;

        @ApiModelProperty(value = "库存批次",example = "12341234",position = 1)
        private Long iabId;

        @ApiModelProperty(value = "库存数",example = "132124",position = 2)
        private BigDecimal ivQty;

        @ApiModelProperty(value = "大包数",example = "100",position = 3)
        private  BigDecimal bigBagQty;

        @ApiModelProperty(value = "中包数(库存数/大包转化率的余数)",example = "10",position = 4)
        private  BigDecimal midBagQty;

        @ApiModelProperty(value = "小包数(库存数/中包转换率的余数)",example = "1",position = 5)
        private  BigDecimal tinyBagQty;
    }
}

