package com.telei.wms.project.api.endpoint.backlc.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/8/26 10:15
 */
@Data
public class BacklcOutStockContainerListRequest {
    @ApiModelProperty(value = "出库任务id",example = "1234123",position = 1)
    @Check
    private Long dohId;
    @Check
    @ApiModelProperty(value = "公司id",example = "132423",position = 2)
    private Long companyId;
    @Check
    @ApiModelProperty(value = "仓库id",example = "3214124214",position = 3)
    private long warehouseId;
}
