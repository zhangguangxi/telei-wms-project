package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
* wms_pao_line 上架单明细
*/
@Data
public class WmsPaoLine implements Entity<Long> {
    /** id */
    private Long paolId;
    /** 单头id */
    private Long paoId;
    /** 入库单单头id */
    private Long roId;
    /** 对应计划明细的id */
    private Long rolId;
    /** 收货作单头id */
    private Long rooId;
    /** 收货单号 */
    private String rooCode;
    /** 收货单收货明细id */
    private Long roolId;
    /** 产品id */
    private Long productId;
    /** 单位毛重(KG) */
    private BigDecimal unitGrossWeight;
    /** 单位体积(CBM) */
    private BigDecimal unitVol;
    /** 备注 */
    private String memo;
    /** 上架数量 */
    private BigDecimal paolQty;
    /** 计量单位 */
    private Integer stockUnit;
    /** 明细行总重量(KG) */
    private BigDecimal lineTotalWeight;
    /** 明细行净重(KG) */
    private BigDecimal lineNetWeight;
    /** 明细行总体积(CBM) */
    private BigDecimal lineTotalVol;
    /** 上架状态  01-制单，20-已上架 */
    private String paoStatus;
    /** 创建用户 */
    private String createUser;
    /** 创建时间 */
    private Date createTime;
    /** 库存批次id */
    private Long iabId;
    /** 推荐库位 */
    private String prepLcCode;
    /** 上架库位 */
    private String lcCode;
    /** 先进先出时间 */
    private Date paolFifoTime;
    /** 上架时间 */
    private Date putawayTime;
    /** 上架用户 */
    private String putawayUser;
    @Override
    public Long getId() {
        return paolId;
    }
    @Override
    public void setId(Long id) {
        this.paolId = id;
    }
}