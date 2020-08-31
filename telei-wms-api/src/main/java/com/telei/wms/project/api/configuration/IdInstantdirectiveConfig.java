package com.telei.wms.project.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Dean
 * @Date: 2020/7/9 17:30
 */
@Data
@Component
@ConfigurationProperties(prefix = "id-instantdirective")
public class IdInstantdirectiveConfig {

    private String serverType;
    private String serverName;
    private String system;
    private Map<String, List<String>> channels = new HashMap<>();
    private Map<String, String> nodes = new HashMap<>();
    private Map<String, String> dataDependencies = new HashMap<>();
}
