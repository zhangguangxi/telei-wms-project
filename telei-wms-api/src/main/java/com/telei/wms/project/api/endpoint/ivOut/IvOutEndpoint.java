package com.telei.wms.project.api.endpoint.ivOut;

import com.nuochen.framework.app.gateway.GatewayConstants;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 待出库存
 * @Auther: Dean
 * @Date: 2020/9/23 11:20
 */
@Api(tags = "待出库存")
@RestController
@RequestMapping(GatewayConstants.INTERNAL_REQUEST_MAPPING)
public class IvOutEndpoint {
}
