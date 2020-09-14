package com.telei.wms.project.api.configuration;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.component.commons.spring.SpringRequestContext;
import com.telei.infrastructure.component.commons.CustomRequestContext;
import com.telei.infrastructure.component.commons.dto.UserInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/13 13:12
 */
@Configuration
public class FeignConfiguration {
    @Bean
    public FeignBasicRequestInterceptor basicRequestInterceptor() {
        return new FeignBasicRequestInterceptor();

    }



    public static   class FeignBasicRequestInterceptor implements RequestInterceptor {
        public FeignBasicRequestInterceptor() {

        }

        @Override
        public void apply(RequestTemplate template) {
            Map<String, Object> requestHeaderMap = SpringRequestContext.getRequestHeaderMap();
            UserInfo userInfo = CustomRequestContext.getUserInfo();
            if(Objects.nonNull(userInfo)){
                template.header("userInfo", JSON.toJSONString(userInfo));
            }
        }
    }

}


