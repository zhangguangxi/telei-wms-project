package com.telei.wms.project.api.endpoint.location.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LocationPageQueryRequest {

    /**
     * 无锁:NO 有锁:YES 上架锁:PUTAWAY 下架锁:PICKING
     */
    @ApiModelProperty(value = "库位锁 无锁:NO 有锁:YES 上架锁:PUTAWAY 下架锁:PICKING", example = "NO")
    private String lcLock;

    @ApiModelProperty(value = "通道", example = "22")
    private String lcAisle;

    @ApiModelProperty(value = "库位编码", example = "22-10-31")
    private String lcCode;

    @ApiModelProperty(value = "库位类型，数据字典，S 样品库位、Z 高架库位 等", example = "20")
    private String lcType;

    @ApiModelProperty(value = "仓库编码", example = "2525345353")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库id", example = "2525345353")
    private Long warehouseId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;

}