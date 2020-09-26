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
//    @ApiModelProperty(value = "装箱时间")
//    @Check
//    private Date packTime;
//
//    @ApiModelProperty(value = "装箱员")
//    @Check
//    private String packUser;

    @Check
    @ApiModelProperty(value = "公司id")
    private Long companyId;
    @Check
    @ApiModelProperty(value = "仓库id")
    private Long warehouseId;

    @Check
    @ApiModelProperty(value = "出库任务id(订单id)")
    private Long dohId;
}