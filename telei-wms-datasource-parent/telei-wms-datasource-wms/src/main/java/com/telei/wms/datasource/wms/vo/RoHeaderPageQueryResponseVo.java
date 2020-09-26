package com.telei.wms.datasource.wms.vo;

import com.telei.wms.datasource.wms.model.WmsRoHeader;
import lombok.Data;

/**
 * 入库任务分页查询
 */
@Data
public class RoHeaderPageQueryResponseVo extends WmsRoHeader {

    /** 仓库名称 */
    private String warehouseName;
    /** 供应商名称 */
    private String supplierName;
    /** 所属人名称 */
    private String userName;
}
