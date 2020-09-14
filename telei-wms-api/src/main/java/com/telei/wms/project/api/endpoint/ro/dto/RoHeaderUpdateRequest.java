package com.telei.wms.project.api.endpoint.ro.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 入库任务更新Request
 */
@Data
public class RoHeaderUpdateRequest {

    /** 入库任务id */
    @ApiModelProperty(value = "主键id", example = "4675353938365515777")
    private Long id;
    /** 预计入库时间 */
    @ApiModelProperty(value = "预计入库时间", example = "2020-07-01 10:00:00")
    private Date estArriveTime;
    /** 交货时间 */
    @ApiModelProperty(value = "交货时间", example = "2020-07-01 10:00:00")
    private Date deliveryTime;
    /** 备注 */
    @ApiModelProperty(value = "备注", example = "备注")
    private String memo;
    /** 物流公司 */
    @ApiModelProperty(value = "物流公司", example = "物流公司")
    private String logisticsCompany;
    /** 物流单号 */
    @ApiModelProperty(value = "物流单号", example = "物流单号")
    private String trackingNo;
}