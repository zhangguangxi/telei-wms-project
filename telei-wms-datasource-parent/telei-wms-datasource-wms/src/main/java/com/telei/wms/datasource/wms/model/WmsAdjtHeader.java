package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.util.Date;

/**
* wms_adjt_header 库存调整单头
*/
@Data
public class WmsAdjtHeader implements Entity<Long> {
    /** id */
    private Long adjhId;
    /** 业务单据编号-按单据编码规则生成 */
    private String adjhCode;
    /** 公司编码 */
    private String companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 调整类型，YW 移位，KC 库存 */
    private String adjhType;
    /** 库存调整状态 01-制单，20-审核，98-关闭 */
    private String ivihStatus;
    /** 业务日期 */
    private Date bizDate;
    /** 原因 */
    private String reason;
    /** 创建时间 */
    private Date createTime;
    /** 创建用户 */
    private String createUser;
    /** 审核时间 */
    private Date auditTime;
    /** 审核用户 */
    private String auditUser;
    @Override
    public Long getId() {
        return adjhId;
    }
    @Override
    public void setId(Long id) {
        this.adjhId = id;
    }
    /* KEEP_MARK_START */
    /* KEEP_MARK_END */
}
