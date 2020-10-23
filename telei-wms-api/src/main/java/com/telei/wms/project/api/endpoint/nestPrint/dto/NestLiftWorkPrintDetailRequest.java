package com.telei.wms.project.api.endpoint.nestPrint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: leo
 * @date: 2020/10/12 10:04
 */
@Data
public class NestLiftWorkPrintDetailRequest {
    @ApiModelProperty(value = "开始时间", example = "2020-07-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-02 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "产品编码", example = "2525345353")
    private String productNo;

    @ApiModelProperty(value = "产品名称", example = "锤子")
    private String productName;

    @ApiModelProperty(value = "条码", example = "B12345566")
    private String productBarcode;

    @ApiModelProperty(value = "样品库位(升,为原库位; 降，为目标库位)", example = "S1-25-15")
    private String sampleLcCode;

    @ApiModelProperty(value = "操作用户", example = "admin")
    private String operateUser;

    @ApiModelProperty(value = "升降类型 RISE-升库,DROP-降货", example = "RISE")
    private String liftType;

    @ApiModelProperty(value = "升降任务状态 10-待处理，20-已处理，98-关闭", example = "10")
    private String liftStatus;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}
