package com.telei.wms.project.api.endpoint.wmsIdInstantdirective;

import com.nuochen.framework.app.gateway.GatewayConstants;
import com.telei.wms.project.api.ServiceId;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdInstantdirectiveCallbackRequest;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdInstantdirectiveCudBaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: Dean
 * @Date: 2020/7/28 15:45
 */
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class WmsIdInstantdirectiveEndpoint {

    @Autowired
    private WmsIdInstantdirectiveBussiness idInstantdirectiveBussiness;

    /**
     * 数据交互回调
     * @param request
     * @return
     */
    @PostMapping(ServiceId.WMS_ID_INSTANTDIRECTIVE_CALLBACK)
    public WmsIdInstantdirectiveCudBaseResponse callback(@RequestBody WmsIdInstantdirectiveCallbackRequest request) {
        return idInstantdirectiveBussiness.callback(request);
    }
}
