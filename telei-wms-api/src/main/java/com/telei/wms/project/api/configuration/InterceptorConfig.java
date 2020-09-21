package com.telei.wms.project.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 重写拦截配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private CorsInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加跨域拦截
        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
    }
}
