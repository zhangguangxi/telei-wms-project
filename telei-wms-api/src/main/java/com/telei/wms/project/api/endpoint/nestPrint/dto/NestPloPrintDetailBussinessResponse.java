package com.telei.wms.project.api.endpoint.nestPrint.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/12 10:42
 */
@Data
public class NestPloPrintDetailBussinessResponse {
    /**打印编号*/
    private Long printNo;
    /**出库单单据据号*/
    private String doCode;
    /** 拣货单单据号*/
    private String ploCode;
    /**拣货单明细*/
    private List<NestPloPrintDetailLine> list;

    @Data
    public static class NestPloPrintDetailLine {
        /**商品码*/
        private String productNo;
        /**箱规(大包转换率->一个大包有多少个小包)*/
        private Integer bigBagRate;
        /**箱数(大包数量)*/
        private Integer bigBagQty;
        /**推荐库位-样品库位*/
        private String prepLcCode;
    }
}
