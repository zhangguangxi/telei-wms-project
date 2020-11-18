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
    private String dohId;
}
