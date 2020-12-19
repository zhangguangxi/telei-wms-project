package com.telei.wms.project.api.endpoint.doContainer.dto;

import com.nuochen.framework.component.validation.Check;
import lombok.Data;

import java.util.List;

@Data
public class DoContainerAddRequest {

    private List<DoContainerCommonRequest> containerAddList;

    @Check
    private Long dohId;

}