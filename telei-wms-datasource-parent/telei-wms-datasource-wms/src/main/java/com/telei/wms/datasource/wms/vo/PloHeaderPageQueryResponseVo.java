package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsPloHeader;
import lombok.Data;

/**
 * 拣货单分页查询
 */
@Data
public class PloHeaderPageQueryResponseVo extends WmsPloHeader {

    /** 仓库名称 */
    private String warehouseName;
    /** 供应商名称 */
    private String supplierName;
    /** 客户名称 */
    private String customerName;
    /** 拣货员 */
    private String plUser;
}
