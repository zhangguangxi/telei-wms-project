package com.telei.wms.customer.businessNumber.dto;

import lombok.Data;

@Data
public class BusinessNumberRequest {

    //类型
    private String type;
    //公司编号
    private String companyNumber;
    //客户编号
    private String customerNumber;
    //仓库编号
    private String repositoryNumber;
}
