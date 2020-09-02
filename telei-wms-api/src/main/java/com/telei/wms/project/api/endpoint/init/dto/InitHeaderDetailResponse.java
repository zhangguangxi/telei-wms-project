package com.telei.wms.project.api.endpoint.init.dto;

import com.telei.wms.datasource.wms.vo.WmsInitLineVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * wms_init_header 库存初始化单
 */
@Data
public class InitHeaderDetailResponse {
    /** id */
    private Long id;
    /** 业务单据编号-按单据编码规则生成 */
    private String ivihCode;
    /** 库存初始化状态 01-制单，20-审核，98-关闭 */
    private String ivihStatus;
    /** 公司编码 */
    private Long companyId;
    /** 仓库id */
    private Long warehouseId;
    /** 仓库code */
    private String warehouseCode;
    /** 备注 */
    private String memo;
    /** 创建时间 */
    private Date createTime;
    /** 创建用户 */
    private String createUser;
    /** 审核时间 */
    private Date auditTime;
    /** 审核用户 */
    private String auditUser;

    private List<WmsInitLineVO> initLines;
}