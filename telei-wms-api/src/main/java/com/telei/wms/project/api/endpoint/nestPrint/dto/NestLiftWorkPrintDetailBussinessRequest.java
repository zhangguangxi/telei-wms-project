package com.telei.wms.project.api.endpoint.nestPrint.dto;

import lombok.Data;

/**
 * @author: leo
 * @date: 2020/10/12 10:04
 */
@Data
public class NestLiftWorkPrintDetailBussinessRequest {
    /**
     * 开始时间【创建时间】
     */
    private String startTime;

    /**
     * 结束时间【创建时间】
     */
    private String endTime;

    /**
     * 产品编码
     */
    private String productNo;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 条码
     */
    private String productBarcode;

    /**
     * 样品库位(升,为原库位; 降，为目标库位)
     */
    private String sampleLcCode;

    /**
     * 升降类型 RISE-升库,DROP-降货
     */
    private String liftType;

    /**
     * 升降任务状态 10-待处理，20-已处理，98-关闭
     */
    private String liftStatus;

    /**
     * 操作用户
     */
    private String operateUser;

    /**
     * 当前页
     */
    private Integer pageNumber;

    /**
     * 每页大小
     */
    private Integer pageSize;
}
