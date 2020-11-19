package com.telei.wms.project.api.endpoint.backlc.dto;

import lombok.Data;

/**
 * @author: leo
 * @date: 2020/8/26 10:11
 */
@Data
public class BacklcListBussinessRequest {
    /**出库任务单头id*/
    private Long dohId;
    /**公司id*/
    private Long companyId;
    /**仓库id*/
    private Long warehouseId;
}
