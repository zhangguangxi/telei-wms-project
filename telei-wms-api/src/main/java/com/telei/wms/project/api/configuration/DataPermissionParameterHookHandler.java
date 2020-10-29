package com.telei.wms.project.api.configuration;

import com.nuochen.framework.app.gateway.GatewayContext;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.datapermission.row.DataPermissionParameterRowHook;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @Auther: leo
 * @Date: 2020/7/28 23:02
 */
//@Component
public class DataPermissionParameterHookHandler implements DataPermissionParameterRowHook {
    @Autowired
    private Caches caches;

    @Override
    public String getDataPermissionFilterRowSql() {
        String serviceId = GatewayContext.getServiceId();
        Long accountId = CustomRequestContext.getUserInfo().getAccountId();
        String dataPermissionSqlKey = accountId + "_" + serviceId;
//        Map<String, String> dataRightsParameter1 = caches.getUserInfoCache().get(accountId).getDataRightsParameter();
        Map<String, String> dataRightsParameter = CustomRequestContext.getUserInfo().getRowDataRightsParameter();
        if(Objects.isNull(dataRightsParameter)){
            return null;
        }
        return dataRightsParameter.get(dataPermissionSqlKey);
    }
}
