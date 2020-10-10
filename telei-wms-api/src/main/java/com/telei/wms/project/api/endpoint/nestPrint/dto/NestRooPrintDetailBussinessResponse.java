package com.telei.wms.project.api.endpoint.nestPrint.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/10 15:13
 */
@Data
public class NestRooPrintDetailBussinessResponse {
    /**打印编号*/
    private Long printNo;
    /**入库单据号*/
    private String roCode;
    /**供应商名称*/
    private String supplierName;
    /**收货单明细*/
    private List<NestRooPrintDetailLine> list;


    @Data
    public static class  NestRooPrintDetailLine{
        /**商品码*/
        private String productNo;
        /**箱规(大包转换率->一个大包有多少个小包)*/
        private Integer bigBagRate;
        /**箱数(大包数量)*/
        private Integer bigBagQty;
    }
}
