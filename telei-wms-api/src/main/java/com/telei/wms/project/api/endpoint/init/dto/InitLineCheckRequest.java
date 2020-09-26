package com.telei.wms.project.api.endpoint.init.dto;

import lombok.Data;

import java.util.List;

/**
 * 产品检查Request
 */
@Data
public class InitLineCheckRequest {

    private List<InitLineDetailCheckRequest> initLineDetails;

}