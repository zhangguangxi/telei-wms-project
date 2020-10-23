package com.telei.wms.project.api.endpoint.nestPrint.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/12 11:27
 */
@Data
public class NestCheckPrintDetailResponse {
    @ApiModelProperty(value = "打印编号",example = "13412341321234",position = 1)
    private Long printNo;
    @ApiModelProperty(value = "出库单单据据号",example = "xx13212342",position = 2)
    private String doCode;
    @ApiModelProperty(value = "出库任务明细",position =  3)
    private List<NestDoPrintDetailLine> list;

    @Data
    public static class NestDoPrintDetailLine {
        @ApiModelProperty(value = "商品码",example = "1234123",position = 1)
        private String productNo;
        @ApiModelProperty(value ="箱规(大包转换率->一个大包有多少个小包)",example = "122",position = 2)
        private Integer bigBagRate;
        @ApiModelProperty(value ="箱数(大包数量)",example = "122",position = 3)
        private Integer bigBagQty;
        @ApiModelProperty(value ="样品位",example = "s_1_122",position = 4)
        private String lcCode;
    }
}
