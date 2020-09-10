package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/10 17:12
 */
@Data
public class InventoryDetailRequest {
    @ApiModelProperty(value = "库位编码",example = "123143",position = 1)
    @Check
    private String lcCode;
    @ApiModelProperty(value = "产品id",example = "234232",position = 2)
    @Check
    private String productId;
}
