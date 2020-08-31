package com.telei.wms.project.api.configuration;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: leo
 * @Date: 2020/6/24 10:21
 */
@Configuration
public class SpringMvcConfigure {
    /**
     * 解决Jackson导致Long型数据精度丢失问题
     *
     * @return
     */
    @Bean("jackson2ObjectMapperBuilderCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Jackson2ObjectMapperBuilderCustomizer customizer =
                jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                        .serializerByType(Long.class, ToStringSerializer.instance)
                        .serializerByType(Long.TYPE, ToStringSerializer.instance);
        return customizer;
    }
}
