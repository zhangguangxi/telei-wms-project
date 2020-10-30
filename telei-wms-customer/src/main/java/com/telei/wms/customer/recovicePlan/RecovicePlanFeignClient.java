package com.telei.wms.customer.recovicePlan;

import com.nuochen.framework.app.api.ApiResponse;
import com.telei.wms.customer.recovicePlan.dto.RecovicePlanAddByDoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Auther: Dean
 * @Date: 2020/10/29 19:40
 */
@FeignClient(value = "RecovicePlanFeignClient", url = "${customize.feign.oms-url}")
public interface RecovicePlanFeignClient {

    /**
     * 出库任务新增入库计划
     * @param request
     * @return
     */
    @PostMapping("internal/021308")
    ApiResponse addRecovicePlanByDo(RecovicePlanAddByDoRequest request);
}
