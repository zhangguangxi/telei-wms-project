package com.telei.wms.project.api.endpoint.liftWork.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * wms_lift_work 升降任务
 */
@Data
public class LiftWorkUpdateCommonRequest {
    private Long id;

    @ApiModelProperty(value = "实际库位",example = "12345678",position = 7)
    @Check
    private String lcCode;

    @ApiModelProperty(value = "操作用户ID",example = "12345678",position = 7)
    @Check
    private Long operateUserId;

    @ApiModelProperty(value = "操作用户",example = "admin",position = 7)
    @Check
    private String operateUser;

    @ApiModelProperty(value = "公司ID",example = "12345678",position = 7)
    private Long companyId;

    @ApiModelProperty(value = "仓库ID",example = "12345678",position = 7)
    private Long warehouseId;

    @ApiModelProperty(value = "仓库编码",example = "12345678",position = 7)
    private String warehouseCode;

    @ApiModelProperty(value = "升降货类型",example = "12345678",position = 7)
    private String liftType;

    @ApiModelProperty(value = "产品id",example = "12345678",position = 7)
    private Long productId;

    @ApiModelProperty(value = "产品编码",example = "12345678",position = 7)
    private String productNo;

    @ApiModelProperty(value = "产品名称",example = "12345678",position = 7)
    private String productName;

    @ApiModelProperty(value = "产品本地名称",example = "12345678",position = 7)
    private String productNameLocal;

    @ApiModelProperty(value = "产品条码",example = "12345678",position = 7)
    private String productBarcode;

    @ApiModelProperty(value = "产品UPC码",example = "12345678",position = 7)
    private String productUpcCode;

    @ApiModelProperty(value = "数量",example = "12345678",position = 7)
    @Check
    private BigDecimal liftQty;

    @ApiModelProperty(value = "大包数量",example = "12345678",position = 7)
    private BigDecimal bigBagQty;

    @ApiModelProperty(value = "大包转换数",example = "12345678",position = 7)
    private BigDecimal bigBagRate;

    @ApiModelProperty(value = "大包剩余数量",example = "12345678",position = 7)
    private BigDecimal bigBagExtraQty;

    @ApiModelProperty(value = "样品库位",example = "12345678",position = 7)
    private String sampleLcCode;

    @ApiModelProperty(value = "推荐库位",example = "12345678",position = 7)
    private String prepLcCode;

    @ApiModelProperty(value = "备注",example = "12345678",position = 7)
    private String memo;

}