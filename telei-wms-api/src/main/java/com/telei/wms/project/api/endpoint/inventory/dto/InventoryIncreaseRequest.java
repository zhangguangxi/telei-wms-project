package com.telei.wms.project.api.endpoint.inventory.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: leo
 * @date: 2020/8/26 10:11
 */
@Data
public class InventoryIncreaseRequest {
    @ApiModelProperty(value = "公司编码",example = "12342123",position = 1)
    @Check
    private String companyId;
    @ApiModelProperty(value = "仓库id",example = "123412342",position = 2)
    @Check
    private Long warehouseId;

    @ApiModelProperty(value = "仓库code",example = "13223",position = 3)
    private String warehouseCode;

    @ApiModelProperty(value = "原因",example = "xxx",position = 4)
    private String reason;

    @ApiModelProperty(value = "商品id",example = "1234123",position = 5)
    @Check
    private Long productId;

    @ApiModelProperty(value = "库位编码",example = "123423",position = 6)
    @Check
    private String lcCode;

    @ApiModelProperty(value = "库存数量",example = "10",position = 7)
    @Check
    private BigDecimal ivQty;

    @ApiModelProperty(value = "调整库位",example = "12314",position = 8)
    private String lcCodeAdjt;

    @ApiModelProperty(value = "调整数量",example = "2343",position = 9)
    @Check
    private BigDecimal ivQtyAdjt;

    @ApiModelProperty(value = "调整明细类型(下拉框)",example = "调增、盘盈",position = 10)
    @Check
    private String adjhDetailType;
}
