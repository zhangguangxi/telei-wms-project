package com.telei.wms.project.api.endpoint.location.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_roo_header 收货单
 */
@Data
public class LocationCommonRequest {

    @ApiModelProperty(value = "库位编码",example = "22-22-22",position = 2)
    @Check
    private String lcCode;

    @ApiModelProperty(value = "仓库id",example = "2",position = 4)
    @Check
    private Long warehouseId;

    @ApiModelProperty(value = "仓库code",example = "2",position = 5)
    @Check
    private String warehouseCode;

    @ApiModelProperty(value = "通道",example = "2",position = 3)
    @Check
    private String lcAisle;

    @ApiModelProperty(value = "货架",example = "2",position = 6)
    @Check
    private String lcX;

    @ApiModelProperty(value = "层",example = "2",position = 6)
    @Check
    private String lcY;

    @ApiModelProperty(value = "列",example = "2",position = 6)
    @Check
    private String lcZ;

    @ApiModelProperty(value = "库位类型，数据字典，S 样品库位、Z 高架库位 等",example = "S",position = 6)
    @Check
    private String lcType;

    @ApiModelProperty(value = "长(CM)",example = "2",position = 6)
    private BigDecimal lcLength;

    @ApiModelProperty(value = "宽(CM)",example = "2",position = 6)
    private BigDecimal lcWidth;

    @ApiModelProperty(value = "高(CM)",example = "2",position = 6)
    private BigDecimal lcHeight;

    @ApiModelProperty(value = "体积CBM",example = "2",position = 6)
    @Check
    private BigDecimal lcVol;

    @ApiModelProperty(value = "承重(kg)",example = "2",position = 6)
    @Check
    private Integer lcSustainweight;

    @ApiModelProperty(value = "上架锁",example = "1",position = 6)
    private String lcPutawaylock;

    @ApiModelProperty(value = "下架锁",example = "0",position = 6)
    private String lcPickinglock;

    @ApiModelProperty(value = "备注",example = "2",position = 6)
    private String memo;

}