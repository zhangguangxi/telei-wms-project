package com.telei.wms.project.api.endpoint.init.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * wms_init_header 库存初始化单头
 */
@Data
public class InitHeaderAddRequest {

    @ApiModelProperty(value = "id",example = "1212121212",position = 1)
    private Long id;

    @ApiModelProperty(value = "业务单据编号",example = "WMS00000001",position = 2)
    private String ivihCode;

    @ApiModelProperty(value = "库存初始化状态 01-制单，20-审核，98-关闭",example = "01",position = 3)
    private String ivihStatus;

    @ApiModelProperty(value = "公司编码",example = "12121212",position = 4)
    private Long companyId;

    @ApiModelProperty(value = "仓库id",example = "12121212",position = 5)
    @Check
    private Long warehouseId;

    @ApiModelProperty(value = "仓库code",example = "SZ",position = 6)
    @Check
    private String warehouseCode;

    @ApiModelProperty(value = "备注",example = "备注",position = 7)
    private String memo;

    private List<InitLineAddRequest> initLines;
}