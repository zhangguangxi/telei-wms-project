package com.telei.wms.project.api.endpoint.roo.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * wms_roo_header 收货单
 */
@Data
public class RooHeaderCommonRequest {
    @ApiModelProperty(value = "入库任务id",example = "2",position = 7)
    @Check
    private Long roId;

    @ApiModelProperty(value = "入库任务单号",example = "2",position = 7)
    @Check
    private String roCode;

    @ApiModelProperty(value = "商家订单号",example = "2",position = 7)
    @Check
    private String custOrderNo;

    @ApiModelProperty(value = "仓库id",example = "2",position = 7)
    @Check
    private Long warehouseId;

    @ApiModelProperty(value = "仓库code",example = "2",position = 7)
    @Check
    private String warehouseCode;

    @ApiModelProperty(value = "供应商id",example = "2",position = 7)
    private Long supplierId;

    @ApiModelProperty(value = "客户id",example = "2",position = 7)
    @Check
    private Long customerId;

    @ApiModelProperty(value = "总数量",example = "2",position = 7)
    @Check
    private BigDecimal totalQty;

    @ApiModelProperty(value = "备注",example = "2",position = 7)
    private String memo;

    @ApiModelProperty(value = "收货用户",example = "2",position = 7)
    @Check
    private String recvUser;

    @ApiModelProperty(value = "收货时间",example = "2020-10-25 10:00:00",position = 7)
    @Check
    private Date recvTime;

    @ApiModelProperty(value = "新增收货列表",example = "",position = 7)
    @Check
    private List<RooLineAddRequest> rooLines;
}