package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.util.Date;

/**
* wms_goods 
*/
@Data
public class Goods implements Entity<Long> {
    private Long id;
    private String name;
        /** 创建时间 */
    private Date createTime;
        /** 客户id */
    private Long customerId;
}