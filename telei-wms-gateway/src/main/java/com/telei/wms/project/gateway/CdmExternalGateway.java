package com.telei.wms.project.gateway;

import com.alibaba.fastjson.JSON;
import com.nuochen.framework.app.api.ApiException;
import com.nuochen.framework.app.api.ApiResponse;
import com.nuochen.framework.app.gateway.hook.GatewayExternalRequestHook;
import com.nuochen.framework.component.commons.spring.SpringRequestContext;
import com.telei.wms.customer.auth.AuthFeignClient;
import com.telei.wms.customer.auth.dto.AuthVerifyFeignRequest;
import com.telei.infrastructure.component.commons.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Auther: leo
 * @Date: 2020/6/14 19:07
 */
@Component
public class CdmExternalGateway implements GatewayExternalRequestHook {
    @Autowired
    private AuthFeignClient authFeignClient;

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(Objects.isNull(token)){
            token =  request.getParameter("token");
            if(Objects.isNull(token)){
                throw new ApiException(ApiResponse.invalidToken());
            }
        }
        String serviceId = request.getRequestURI().split("/")[2];
        AuthVerifyFeignRequest feignRequest = new AuthVerifyFeignRequest();
        feignRequest.setToken(token);
        feignRequest.setServiceId(serviceId);
        ApiResponse apiResponse = authFeignClient.authVerify(feignRequest);
        UserDto userInfo = apiResponse.convertDataToObject(UserDto.class);
        request.setAttribute("userInfo",userInfo);
        return true;
    }
}
