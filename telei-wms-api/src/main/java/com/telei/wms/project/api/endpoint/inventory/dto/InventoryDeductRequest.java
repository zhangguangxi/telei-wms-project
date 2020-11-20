package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/21 16:14
 */
@Data
public class InventoryDeductRequest {
    @ApiModelProperty(value ="出库类型 01-补货入库 02-内部订单 08-销售退货",example = "08",position = 1)
    private String orderType;
    @Check
    @ApiModelProperty(value = "公司id",position = 2)
    private Long companyId;
    @Check
    @ApiModelProperty(value = "仓库id",position = 3)
    private Long warehouseId;

    @Check
    @ApiModelProperty(value = "出库任务id(订单id)",position = 4)
    private Long dohId;
}
