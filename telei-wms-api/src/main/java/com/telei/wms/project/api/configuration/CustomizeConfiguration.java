package com.telei.wms.project.api.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 自定义配置
 */
@Configuration
@EnableConfigurationProperties(CustomizeProperties.class)
public class CustomizeConfiguration {
    private final CustomizeProperties customizeProperties;

    public CustomizeConfiguration(CustomizeProperties customizeProperties) {
        this.customizeProperties = customizeProperties;
    }

}
