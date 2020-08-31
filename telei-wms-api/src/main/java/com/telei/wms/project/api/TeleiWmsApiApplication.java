package com.telei.wms.project.api;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.nuochen" , "com.telei"})
@EnableFeignClients(basePackages = {"com.telei"})
@EnableSwagger2Doc
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = "com.telei")
public class TeleiWmsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeleiWmsApiApplication.class, args);
    }
}
