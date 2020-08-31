package com.telei.wms.customer.syncPermissions.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description: token校验请求体
 * @Auther: leo
 * @Date: 2020/6/18 10:27
 */
@Data
public class SyncPermissionFeignRequest {
    /**权限项信息*/
    List<Map<String, Object>> resourceRights;
}
