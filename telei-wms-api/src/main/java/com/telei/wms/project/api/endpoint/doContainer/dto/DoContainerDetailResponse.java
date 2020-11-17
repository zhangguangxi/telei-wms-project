package com.telei.wms.project.api.endpoint.doContainer.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoContainerDetailResponse {

    private List<DoContainerCommonResponse> containerDetailList;

}