package com.telei.wms.project.api.endpoint.nestPrint.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/12 10:04
 */
@Data
public class NestLiftWorkPrintDetailResponse {
    @ApiModelProperty(value ="打印编号",example = "x13413",position = 1)
    private Long printNo;
//    @ApiModelProperty(value = "升降任务类型(1:升任务  2：降任务)",example = "1",position = 2)
//    private Integer leftWorkType;
    @ApiModelProperty(value = "升降任务明细",position = 3)
    private List<NestLiftWorkPrintDetailBussinessResponse.NestLiftWorkPrintDetailLine> list;

    @Data
    public static class NestLiftWorkPrintDetailLine {
        @ApiModelProperty(value = "商品码",position = 1)
        private String productNo;
        @ApiModelProperty(value = "箱数(大包数量)",position = 2)
        private Integer bigBagQty;
        @ApiModelProperty(value = "升降类型 RISE-升货,DROP-降货",position = 3)
        private String liftType;
        @ApiModelProperty(value = "样品库位(升,为原库位; 降，为目标库位)",position = 4)
        private String sampleLcCode;
        @ApiModelProperty(value = "推荐库位",position = 5)
        private String prepLcCode;
        @ApiModelProperty(value = "单据ID",position = 6)
        private Long orderId;
        @ApiModelProperty(value = "单据编号",position = 7)
        private String orderCode;
    }
}
