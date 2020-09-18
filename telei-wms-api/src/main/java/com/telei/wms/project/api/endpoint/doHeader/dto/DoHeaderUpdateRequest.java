package com.telei.wms.project.api.endpoint.doHeader.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 出库任务更新Request
 */
@Data
public class DoHeaderUpdateRequest {

    /** 出库任务id */
    @ApiModelProperty(value = "主键id", example = "4675353938365515777")
    private Long id;
    /** 核验时间 */
    @ApiModelProperty(value = "核验时间", example = "2020-07-01 10:00:00")
    private Date checkTime;
    /** 发货时间 */
    @ApiModelProperty(value = "发货时间", example = "2020-07-01 10:00:00")
    private Date shippingTime;
    /** 备注 */
    @ApiModelProperty(value = "备注", example = "备注")
    private String memo;
}