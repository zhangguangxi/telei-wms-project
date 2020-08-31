package com.telei.wms.customer.businessNumber.dto;

import lombok.Data;

@Data
public class BusinessNumberListRequest extends BusinessNumberRequest {

    //业务单号数量
    private Integer total;
}
