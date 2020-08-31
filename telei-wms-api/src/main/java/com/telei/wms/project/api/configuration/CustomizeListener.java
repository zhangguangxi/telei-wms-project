package com.telei.wms.project.api.configuration;

import com.google.common.util.concurrent.MoreExecutors;
import com.telei.wms.customer.syncPermissions.SyncPermissionFeignClient;
import com.telei.wms.customer.syncPermissions.dto.SyncPermissionFeignRequest;
import com.telei.wms.project.api.ServiceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.telei.infrastructure.component.commons.utils.BusinessUtil.getResourceRights;

/**
 * 自定义监听器
 */
@Component
public class CustomizeListener implements ApplicationListener {
    @Autowired
    private CustomizeProperties customizeProperties;

    @Autowired
    private SyncPermissionFeignClient syncPermissionFeignClient;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            onReady((ApplicationReadyEvent) event);
            return;
        }
        if (event instanceof ApplicationFailedEvent) {
            onFailed((ApplicationFailedEvent) event);
        }
    }

    private void onReady(ApplicationReadyEvent event) {
        final ConfigurableApplicationContext context = event.getApplicationContext();
        MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor()).submit(()->{
            //权限项初始化
            PermissionInit();
        });
    }

    private void PermissionInit(){
        Boolean autoSync = customizeProperties.getPermissionSwitch().getAutoSync();
        if(!autoSync){
            return;
        }
        List<Map<String, Object>> resourceRights = null;
        try {
            resourceRights = getResourceRights(ServiceId.class);
            if(resourceRights.isEmpty() || resourceRights.size() == 0){
                return;
            }
            //远程调用接口
            SyncPermissionFeignRequest syncPermissionFeignRequest = new SyncPermissionFeignRequest();
            syncPermissionFeignRequest.setResourceRights(resourceRights);
            syncPermissionFeignClient.syncPermissions(syncPermissionFeignRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void onFailed(ApplicationFailedEvent event) {
    }
}
