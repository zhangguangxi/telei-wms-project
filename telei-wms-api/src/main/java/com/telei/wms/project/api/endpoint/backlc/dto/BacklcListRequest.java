package com.telei.wms.project.api.endpoint.backlc.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/8/26 10:11
 */
@Data
public class BacklcListRequest {
    @Check
    @ApiModelProperty(value = "出库任务单头id",example = "1234134",position = 1)
    private Long dohId;
    @Check
    @ApiModelProperty(value = "公司id",example = "13143",position = 2)
    private Long companyId;
    @Check
    @ApiModelProperty(value = "仓库id",example = "1231234",position = 3)
    private Long warehouseId;
}
