package com.telei.wms.project.api.endpoint.goods.dto;


import lombok.Data;

/**
 * @Description:
 * @Auther: leo
 * @Date: 2020/6/9 14:22
 */
@Data
public class GoodsBusinessPageQueryRequest /*extends DataPermissionRequest*/ {
    /**当前页*/
    private Integer pageNumber;
    /**每页记录数*/
    private Integer pageSize;
}
