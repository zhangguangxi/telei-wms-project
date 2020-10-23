package com.telei.wms.project.api.endpoint.nestPrint.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/12 10:04
 */
@Data
public class NestLiftWorkPrintDetailBussinessResponse {
    /**打印编号*/
    private Long printNo;
//    /***升降任务类型*/
//    private Integer leftWorkType;
    /**升降任务明细*/
    private List<NestLiftWorkPrintDetailLine> list;

    @Data
    public static class NestLiftWorkPrintDetailLine {
        /**商品码*/
        private String productNo;
        /**升降类型 RISE-升库,DROP-降货*/
        private String liftType;
        /**箱数(大包数量)*/
        private Integer bigBagQty;
        /**样品库位(升,为原库位; 降，为目标库位)*/
        private String sampleLcCode;
        /**推荐库位*/
        private String prepLcCode;
    }
}
