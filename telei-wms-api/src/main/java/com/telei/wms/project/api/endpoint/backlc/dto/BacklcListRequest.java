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
    @ApiModelProperty(value = "出库任务单头id",example = "1234134")
    @Check
    private Long dohId;
}
