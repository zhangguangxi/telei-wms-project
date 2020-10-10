package com.telei.wms.project.api.endpoint.nestPrint.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/10 14:14
 */
@Data
public class NestRooPrintDetailResponse {
    @ApiModelProperty(value = "打印编号",example = "134142342134",position = 1)
    private String printNo;
    @ApiModelProperty(value = "入库单号",example = "RK132143223",position = 2)
    private String roCode;
    @ApiModelProperty(value = "供应商名称",example = "RK132143223",position = 3)
    private String supplierName;
    @ApiModelProperty(value = "收货单明细",position = 4)
    private List<NestRooPrintDetailLine> list;

    @Data
    public static class  NestRooPrintDetailLine{
        @ApiModelProperty(value = "商品码",example = "132412341234",position = 1)
        private String productNo;
        @ApiModelProperty(value = "箱规(大包转换率->一个大包有多少个小包)",example = "132412341234",position = 2)
        private Integer bigBagRate;
        @ApiModelProperty(value = "箱数(大包数量)",example = "5354",position = 3)
        private Integer bigBagQty;
    }
}
