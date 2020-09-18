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
    @ApiModelProperty(value = "公司id",example = "12341234",position = 1)
    @Check
    private Long companyId;
    @ApiModelProperty(value = "仓库id",example = "13241324",position = 2)
    private Long warehouseId;
    @ApiModelProperty(value = "库位编码",example = "123143",position = 3)
    @Check
    private String lcCode;
    @ApiModelProperty(value = "产品id",example = "234232",position = 4)
    @Check
    private Long productId;
}
