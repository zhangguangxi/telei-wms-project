package com.telei.wms.project.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义属性
 */
@Data
@ConfigurationProperties(prefix = "customize")
public class CustomizeProperties {
    private PermissionSwitch permissionSwitch;

    @Data
    public static  class PermissionSwitch {
        private Boolean autoSync;
    }
}
