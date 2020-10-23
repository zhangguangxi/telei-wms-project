package com.telei.wms.project.api.configuration;

import com.nuochen.framework.app.gateway.GatewayContext;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.datapermission.DataPermissionParameterHook;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 *
 * @Auther: leo
 * @Date: 2020/7/28 23:02
 */
//@Component
public class DataPermissionParameterHookHandler implements DataPermissionParameterHook {
    @Autowired
    private Caches caches;

    @Override
    public String getDataPermissionFilterSql() {
        String serviceId = GatewayContext.getServiceId();
        Long accountId = CustomRequestContext.getUserInfo().getAccountId();
        String dataPermissionSqlKey = accountId + "_" + serviceId;
//        Map<String, String> dataRightsParameter1 = caches.getUserInfoCache().get(accountId).getDataRightsParameter();
        Map<String, String> dataRightsParameter = CustomRequestContext.getUserInfo().getDataRightsParameter();
        return dataRightsParameter.get(dataPermissionSqlKey);
    }
}
