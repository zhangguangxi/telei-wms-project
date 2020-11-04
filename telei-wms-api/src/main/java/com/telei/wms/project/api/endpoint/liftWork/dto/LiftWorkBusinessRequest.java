package com.telei.wms.project.api.endpoint.liftWork.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author gongrp
 * @date 2020/6/10 16:52
 */
@Data
public class LiftWorkBusinessRequest {

    private Long id;

    private Long companyId;

    private Long warehouseId;

    private String warehouseCode;

    private String liftType;

    private Long productId;

    private String productNo;

    private String productName;

    private String productNameLocal;

    private String productBarcode;

    private String productUpcCode;

    private BigDecimal liftQty;

    private BigDecimal bigBagQty;

    private BigDecimal bigBagRate;

    private BigDecimal bigBagExtraQty;

    private String sampleLcCode;

    private String prepLcCode;

    private String lcCode;

    private String liftStatus;

    private Long operateUserId;

    private String operateUser;

    private String memo;

    private String liftCode;

    private Collection<Long> ids;

    private List<LiftWorkCommonRequest> updateRequestList;

    private List<LiftWorkCommonRequest> addRequestList;

}
