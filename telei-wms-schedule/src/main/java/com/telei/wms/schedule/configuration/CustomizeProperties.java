package com.telei.wms.schedule.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义属性
 */
@Data
@ConfigurationProperties(prefix = "customize.api")
public class CustomizeProperties {

}
