package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/21 16:14
 */
@Data
public class InventoryDeductBussinessRequest {
    /**出库类型 01-补货入库 02-内部订单 08-销售退货*/
    private String orderType;
    /**出库任务id(订单id)*/
    private Long dohId;
    /**公司id*/
    private Long companyId;
    /**仓库id*/
    private Long warehouseId;
}
