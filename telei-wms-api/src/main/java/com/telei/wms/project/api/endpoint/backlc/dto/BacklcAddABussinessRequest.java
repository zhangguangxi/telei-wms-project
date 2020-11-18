package com.telei.wms.project.api.endpoint.backlc.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/8/26 10:04
 */
@Data
public class BacklcAddABussinessRequest {
    private Long dohId;
    private List<BacklcAddRequest.BacklcAddRequestCondition> list;
    @Data
    public static  class BacklcAddRequestCondition {
        /** 公司编码 */
        private Long companyId;
        /** 仓库id */
        private Long warehouseId;
        /** 仓库code */
        private String warehouseCode;
        /** 出库任务明细id */
        private Long dolId;
        /** 产品id */
        private Long productId;
        /** 库位编码 */
        private String lcCode;
        /** 大包数量 */
        private BigDecimal bigBagQty;
        /** 大包转换数 */
        private BigDecimal bigBagRate;
        /** 中包数量 */
        private BigDecimal midBagQty;
        /** 中包转换数 */
        private BigDecimal midBagRate;
        /** 小包数量 */
        private BigDecimal smallBagQty;
        /** 退库数量 */
        private BigDecimal bQty;
        /** 退库重量(KG) */
        private BigDecimal bWeight;
        /** 退库体积(CBM) */
        private BigDecimal bVol;
    }
}
