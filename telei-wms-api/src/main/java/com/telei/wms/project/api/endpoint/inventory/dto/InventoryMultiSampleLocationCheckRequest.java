package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/11/13 16:47
 */
@Data
public class InventoryMultiSampleLocationCheckRequest {
    private List<InventoryMultiSampleLocationCheckCondition> list;
    @Data
    public static  class InventoryMultiSampleLocationCheckCondition{
        @ApiModelProperty(value = "公司id",example = "123413241",position = 1)
        @Check
        private Long companyId;

        @ApiModelProperty(value = "仓库id",example = "1234123412",position = 2)
        @Check
        private Long warehouseId;

        @ApiModelProperty(value = "产品序列码",example = "12341234123",position = 4)
        @Check
        private Long productId;
    }
}
