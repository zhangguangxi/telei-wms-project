package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
* wms_backlc 退库单
*/
@Data
public class WmsBacklc implements Entity<Long> {
    /** id */
    private Long blId;
    /** 退库单号 */
    private String blCode;
    /** 公司编码 */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 出库任务id */
    private Long dohId;
    /** 货品种类数量 */
    private Integer detailedSpeciesQty;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 中包数量 */
    private BigDecimal midBagQty;
    /** 小包数量 */
    private BigDecimal smallBagQty;
    /** 退库数量 */
    private BigDecimal bQty;
    /** 退库重量(KG) */
    private BigDecimal weight;
    /** 退库体积(CBM) */
    private BigDecimal vol;
    /** 打印次数 */
    private Integer printQty;
    /** 备注 */
    private String memo;
    /** 创建时间 */
    private Date createTime;
    /** 创建用户 */
    private String createUser;
    @Override
    public Long getId() {
        return blId;
    }
    @Override
    public void setId(Long id) {
        this.blId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}
