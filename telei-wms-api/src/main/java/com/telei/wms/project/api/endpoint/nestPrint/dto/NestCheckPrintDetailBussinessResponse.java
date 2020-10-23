package com.telei.wms.project.api.endpoint.nestPrint.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: leo
 * @date: 2020/10/12 11:27
 */
@Data
public class NestCheckPrintDetailBussinessResponse {
    /**打印编号*/
    private Long printNo;
    /**出库任务单单据编号*/
    private String doCode;
    /**出库任务明细*/
    private List<NestDoPrintDetailLine> list;

    @Data
    public static class NestDoPrintDetailLine {
        /**商品码*/
        private String productNo;
        /**箱规(大包转换率->一个大包有多少个小包)*/
        private Integer bigBagRate;
        /**箱数(大包数量)*/
        private Integer bigBagQty;
        /**推荐库位-样品库位*/
        private String lcCode;
    }
}
