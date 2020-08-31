package com.telei.wms.project.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: Dean
 * @Date: 2020/7/28 14:30
 */
@Data
@Component
@ConfigurationProperties(prefix = "wms-id-instantdirective")
public class WmsIdInstantdirectiveConfig {

    private String system;
    private Map<String, String> nodes = new HashMap<>();
}
