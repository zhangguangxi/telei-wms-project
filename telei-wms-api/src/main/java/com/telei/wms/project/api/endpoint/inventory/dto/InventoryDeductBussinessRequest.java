package com.telei.wms.project.api.endpoint.inventory.dto;

import lombok.Data;

/**
 * @author: leo
 * @date: 2020/9/21 16:14
 */
@Data
public class InventoryDeductBussinessRequest {
    /**装箱时间*/
//    private Date packTime;
    /**装箱员*/
//    private String packUser;
    /**出库任务id(订单id)*/
    private Long dohId;
    /**公司id*/
    private Long companyId;
    /**仓库id*/
    private Long warehouseId;
}
