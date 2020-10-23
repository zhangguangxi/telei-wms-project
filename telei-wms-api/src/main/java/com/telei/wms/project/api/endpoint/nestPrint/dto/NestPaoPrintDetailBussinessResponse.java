package com.telei.wms.project.api.endpoint.nestPrint.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/12 09:18
 */
@Data
public class NestPaoPrintDetailBussinessResponse {
    /**打印编号*/
    private Long printNo;
    /**收货单单据据号*/
    private String rooCode;
    /**上架单单据号*/
    private String paoCode;

    /**上架单明细*/
    private List<NestPaoPrintDetailLine> list;

    @Data
    public static class  NestPaoPrintDetailLine{
        /**商品码*/
        private String productNo;
        /**箱规(大包转换率->一个大包有多少个小包)*/
        private Integer bigBagRate;
        /**箱数(大包数量)*/
        private Integer bigBagQty;
        /**推荐库位*/
        private String prepLcCode;
    }
}
