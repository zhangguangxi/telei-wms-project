package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;

/**
* wms_backlc_list 退库单明细
*/
@Data
public class WmsBacklcList implements Entity<Long> {
    /** id */
    private Long bllId;
    /** 单头id */
    private Long blId;
    /** 出库任务明细id */
    private Long dolId;
    /** 出库任务id */
    private Long dohId;
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
    @Override
    public Long getId() {
        return bllId;
    }
    @Override
    public void setId(Long id) {
        this.bllId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}
