package com.telei.wms.project.api.endpoint.pao.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaoHeaderAddRequest {

    /** 入库单单头id */
    @ApiModelProperty(value = "入库单id", example = "4675353938365515777")
    private Long roId;
    /** 入库任务单号 */
    @ApiModelProperty(value = "入库任务单号", example = "365515777")
    private String roCode;
    /** 收货作单头id */
    @ApiModelProperty(value = "收货单id", example = "1")
    private Long rooId;
    /** 收货单号 */
    @ApiModelProperty(value = "收货单号", example = "4675353938365515777")
    private String rooCode;
    /** 商家订单号 */
    @ApiModelProperty(value = "商家订单号", example = "4675353938365515777")
    private String custOrderNo;
    /** 仓库id */
    @ApiModelProperty(value = "仓库id", example = "4675353938365515777")
    private Long warehouseId;
    /** 仓库code */
    @ApiModelProperty(value = "仓库code", example = "4675353938365515777")
    private String warehouseCode;
    /** 总数量 */
    @ApiModelProperty(value = "总数量", example = "1")
    private BigDecimal totalQty;
    /** 总重量(KG) */
    @ApiModelProperty(value = "总重量", example = "1")
    private BigDecimal totalWeight;
    /** 总净重(KG) */
    @ApiModelProperty(value = "总净重", example = "1")
    private BigDecimal totalNetWeight;
    /** 总体积(CBM) */
    @ApiModelProperty(value = "总体积", example = "1")
    private BigDecimal totalVol;
    /** 状态  01-制单，10-部分上架，20-已上架，98-关闭，99-作废 */
    @ApiModelProperty(value = "状态  01-制单，10-部分上架，20-已上架，98-关闭，99-作废", example = "01")
    private String paoStatus;

    private List<PaoLineAddRequest> paoLines;
}