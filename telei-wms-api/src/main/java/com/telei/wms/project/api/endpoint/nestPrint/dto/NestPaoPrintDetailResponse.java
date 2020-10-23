package com.telei.wms.project.api.endpoint.nestPrint.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/12 09:17
 */
@Data
public class NestPaoPrintDetailResponse {
    @ApiModelProperty(value = "打印编号",example = "13412341321234",position = 1)
    private Long printNo;
    @ApiModelProperty(value = "收货单单据据号",example = "xx13212342",position = 2)
    private String rooCode;
    @ApiModelProperty(value = "上架单单据号",example = "ddx13412",position = 3)
    private String paoCode;
    @ApiModelProperty(value = "上架单明细",position =  4)
    private List<NestPaoPrintDetailBussinessResponse.NestPaoPrintDetailLine> list;

    @Data
    public static class  NestPaoPrintDetailLine{
        @ApiModelProperty(value = "商品码",example = "1234123",position = 1)
        private String productNo;
        @ApiModelProperty(value ="箱规(大包转换率->一个大包有多少个小包)",example = "122",position = 2)
        private Integer bigBagRate;
        @ApiModelProperty(value ="箱数(大包数量)",example = "122",position = 3)
        private Integer bigBagQty;
        @ApiModelProperty(value ="推荐库位",example = "s_1_122",position = 4)
        private String prepLcCode;
    }
}
