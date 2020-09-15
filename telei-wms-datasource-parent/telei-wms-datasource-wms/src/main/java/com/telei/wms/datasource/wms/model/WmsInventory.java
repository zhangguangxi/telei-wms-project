package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
* wms_inventory 库存表
*/
@Data
public class WmsInventory implements Entity<Long> {
    /** id */
    private Long ivId;
    /** 公司id */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 库位编码 */
    private String lcCode;
    /** 库位类型，数据字典，S 样品库位、Z 高架库位 等 */
    private String lcType;
    /** 客户id */
    private Long customerId;
    /** 产品序列码 */
    private Long productId;
    /** 库存批次id */
    private Long iabId;
    /** 实物类别，GD合格 BK破损 MD霉变 WE淋湿 DF残次，数据字典COMMON_QS_CODE */
    private String qsCode;
    /** 先进先出时间 */
    private Date ivFifoTime;
    /** 库存数量 */
    private BigDecimal ivQty;
    /** 计量单位 */
    private Long stockUnit;
    /** 中包数量 */
    private BigDecimal midBagQty;
    /** 中包转换数 */
    private Integer midBagRate;
    /** 中包剩余数量 */
    private BigDecimal midBagExtraQty;
    /** 大包数量 */
    private BigDecimal bigBagQty;
    /** 大包转换数 */
    private Integer bigBagRate;
    /** 大包剩余数量 */
    private BigDecimal bigBagExtraQty;
    /** 库存锁，0可以出出货，1不可以出货 */
    private Integer ivLocksign;
    /** 冻结锁，0可以操作，1不可以移库、拆分、出货、更新 */
    private Integer ivFreezesign;
    /** 业务日期 */
    private Date bizDate;
    /** 创建时间 */
    private Date ivCreatetime;
    /** 最近库存更新时间 */
    private Date ivTranstime;
    /** 应用类型代码,引起本次库存变动的来源单据的应用类型  RECV 入库单收货 DOMT 出库订单 ADJT 库存调整单 等 */
    private String apCodeDc;
    /** 引起库存变动业务单据编号 */
    private String ivDocumentCode;
    /** 引起库存变动单据id */
    private Long ivDocumentId;
    /** 引起库存变动单据明细id */
    private Long ivDocumentlineId;
    /** 生产日期 */
    private Date productDate;
    /** 有效日期 */
    private Date ivEffectiveDate;
    /** 客户指定批次号 */
    private String batchNo;
    /** 原库存id */
    private Long ivIdFrom;
    @Override
    public Long getId() {
        return ivId;
    }
    @Override
    public void setId(Long id) {
        this.ivId = id;
    }
}