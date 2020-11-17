package com.telei.wms.project.api.endpoint.backlc.dto;

import com.nuochen.framework.component.validation.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/8/26 09:47
 */
@Data
public class BacklcAddRequest {
    private List<BacklcAddRequestCondition> list;
    @Data
    public static  class BacklcAddRequestCondition {
        @Check
        @ApiModelProperty(value = "公司id",example = "1234123",position = 1)
        private Long companyId;
        /** 仓库id */
        @Check
        @ApiModelProperty(value = "公司id",example = "1234123",position = 2)
        private Long warehouseId;
        @Check
        @ApiModelProperty(value = "仓库code",example = "1234123",position = 3)
        private String warehouseCode;
        @Check
        @ApiModelProperty(value = "出库任务明细id",example = "1234123",position = 4)
        private Long dolId;
        @Check
        @ApiModelProperty(value = "出库任务id",example = "1234",position = 5)
        private Long dohId;
        @Check
        @ApiModelProperty(value = "产品id",example = "1234123",position = 6)
        private Long productId;
        @Check
        @ApiModelProperty(value = "库位编码",example = "1234123",position = 7)
        private String lcCode;
        @Check
        @ApiModelProperty(value = "大包数量",example = "100",position = 8)
        private BigDecimal bigBagQty;
        @Check
        @ApiModelProperty(value = "大包转换数",example = "200",position = 9)
        private BigDecimal bigBagRate;
        @Check
        @ApiModelProperty(value = "中包数量",example = "50",position = 10)
        private BigDecimal midBagQty;
        @Check
        @ApiModelProperty(value = "中包转换数",example = "100",position = 11)
        private BigDecimal midBagRate;
        @Check
        @ApiModelProperty(value = "小包数量",example = "111",position = 12)
        private BigDecimal smallBagQty;
        @Check
        @ApiModelProperty(value = "退库数量",example = "1234123",position = 13)
        private BigDecimal bQty;
        @Check
        @ApiModelProperty(value = "退库重量(KG)",example = "12",position = 14)
        private BigDecimal bWeight;
        @Check
        @ApiModelProperty(value = "退库体积(CBM)",example = "0.1",position = 15)
        private BigDecimal bVol;
    }
}
