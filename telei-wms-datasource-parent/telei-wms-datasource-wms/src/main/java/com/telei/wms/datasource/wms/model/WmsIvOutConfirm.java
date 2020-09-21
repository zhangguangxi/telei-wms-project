package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
* wms_iv_out_confirm 库存锁定扣减记录表
*/
@Data
public class WmsIvOutConfirm implements Entity<Long> {
    /** id */
    private Long ivocId;
    /** ivo_id */
    private Long ivoId;
    /** 库存id */
    private Long ivId;
    /** 扣减次数 */
    private Integer ivIdIndex;
    /** 扣减锁定库存数量 */
    private BigDecimal ivocQty;
    /** 应用类型代码,本次库存锁定的的来源单据的应用类型 D0MT 出库订单  等 */
    private String apCodeDc;
    /** 引起库存变动业务单据编号 */
    private String ivDocumentCode;
    /** 引起库存变动单据id */
    private Long ivDocumentId;
    /** 引起库存变动单据明细id */
    private Long ivDocumentlineId;
    /** 库位 */
    private String lcCode;
    /** 确认时间 */
    private Date confirmTime;
    @Override
    public Long getId() {
        return ivocId;
    }
    @Override
    public void setId(Long id) {
        this.ivocId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}
