package com.telei.wms.project.api.endpoint.inventory.dto;

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
public class InventoryAddRequest {
    private List<InventoryAddRequestCondition> list;
    @Data
    public static  class InventoryAddRequestCondition {
        @ApiModelProperty(value = "公司id",example = "123413241",position = 1)
        @Check
        private Long companyId;
        @ApiModelProperty(value = "仓库id",example = "1234123412",position = 2)
        @Check
        private Long warehouseId;
        @ApiModelProperty(value = "仓库code",example = "12341234",position = 3)
        @Check
        private String warehouseCode;
        @ApiModelProperty(value = "库位编码",example = "12341234",position = 4)
        @Check
        private String lcCode;
        @ApiModelProperty(value = "库存数量",example = "20",position = 5)
        @Check
        private BigDecimal ivQty;
        @ApiModelProperty(value = "商家编码",example = "1234123",position = 6)
        @Check
        private String customerId;
        @ApiModelProperty(value = "产品序列码",example = "12341234123",position = 7)
        @Check
        private Long productId;
        @ApiModelProperty(value = "库存批次id",example = "12342324",position = 8)
        @Check
        private Long iabId;
        @ApiModelProperty(value = "中包数量",example = "133",position = 9)
        @Check
        private BigDecimal midBagQty;
        @ApiModelProperty(value = "大包数量",example = "1234",position = 10)
        @Check
        private BigDecimal bigBagQty;

        @ApiModelProperty(value = "上架单业务单据编号",example = "123412322",position = 11)
        @Check
        private String ivDocumentCode;
        @ApiModelProperty(value = "上架单单头id",example = "1234123",position = 12)
        @Check
        private Long ivDocumentId;
        @ApiModelProperty(value = "上架单明细id",example = "123423",position = 13)
        @Check
        private Long ivDocumentlineId;
    }
}
