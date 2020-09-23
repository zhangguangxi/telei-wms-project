package com.telei.wms.project.api.endpoint.roo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * wms_roo_header 收货单
 */
@Data
public class RooHeaderAddResponse extends RooHeaderCommonResponse{
    /** id */
    private Long id;
}