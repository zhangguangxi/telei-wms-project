package com.telei.wms.project.api.endpoint.backlc.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/8/26 10:11
 */
@Data
public class BacklcListResponse {
    @ApiModelProperty(value = "退库明细记录",position = 1)
    private List<BacklcListBussinessRecord> list;
    @ApiModelProperty(value = "总数量",position = 2)
    private BigDecimal totalBqty;
    @ApiModelProperty(value = "总计数",position = 3)
    private BigDecimal totalBigBagqty;
    @ApiModelProperty(value = "总重量",position = 4)
    private BigDecimal totalWeight;
    @ApiModelProperty(value = "总体积",position = 5)
    private BigDecimal toatalVol;
    @ApiModelProperty(value = "录制日期",position = 6)
    private Date createTime;
    @ApiModelProperty(value = "录制人",position = 7)
    private String createUser;
    @ApiModelProperty(value = "单据编号",position = 8)
    private String blCode;


    @Data
    public static  class  BacklcListBussinessRecord{
        @ApiModelProperty(value = "退库明细id",example = "1231432",position = 1)
        private Long bllId;
        @ApiModelProperty(value = "单头id",example = "1324124123",position = 2)
        private Long blId;
        @ApiModelProperty(value = "出库任务明细id",example ="123142",position = 3)
        private Long dolId;
        @ApiModelProperty(value = "出库任务id",example = "1234122",position = 4)
        private Long dohId;
        @ApiModelProperty(value = "sku编号",example = "1234122",position = 4)
        private String productNo;
        @ApiModelProperty(value = "产品名称",example = "耐克11",position = 5)
        private String productName;
        @ApiModelProperty(value = "图片地址",example = "1124144",position = 6)
        private String mediaId;
        @ApiModelProperty(value = "条码",example = "1124144",position = 7)
        private String productBarcode;
        @ApiModelProperty(value = "品牌",example = "耐克",position = 8)
        private String brand;
        @ApiModelProperty(value = "重量(大包)",example = "1.2",position = 9)
        private BigDecimal bigBagWeight;
        @ApiModelProperty(value = "体积(大包)",example = "22",position = 10)
        private BigDecimal bigBagVol;
        @ApiModelProperty(value = "箱规",example = "耐克",position = 11)
        private BigDecimal boxQty;
        @ApiModelProperty(value = "大包数量(件数)",example = "22",position = 12)
        private BigDecimal bigBagQty;
        @ApiModelProperty(value = "中包数量",example = "33",position = 13)
        private BigDecimal midBagQty;
        @ApiModelProperty(value = "小包数量",example = "34",position = 14)
        private BigDecimal smallBagQty;
        @ApiModelProperty(value = "总数量-退库数(小包数)",example = "44",position = 15)
        private BigDecimal bQty;
        @ApiModelProperty(value = "退库重量(KG)",example = "1342",position = 16)
        private BigDecimal bWeight;
        @ApiModelProperty(value = "退库体积(CBM)",example = "134.3",position = 17)
        private BigDecimal bVol;
        @ApiModelProperty(value = "样品库位",example = "s1234",position = 18)
        private String lcCode;
        @ApiModelProperty(value = "大包转换率",position = 19)
        private BigDecimal bigBagRate;
        @ApiModelProperty(value = "中包转换率",position = 20)
        private BigDecimal midBagRate;
    }
}
