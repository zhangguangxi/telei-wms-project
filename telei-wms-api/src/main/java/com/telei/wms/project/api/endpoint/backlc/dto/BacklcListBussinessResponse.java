package com.telei.wms.project.api.endpoint.backlc.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: leo
 * @date: 2020/8/26 10:12
 */
@Data
public class BacklcListBussinessResponse {
    private List<BacklcListBussinessRecord> list;
    private BigDecimal totalBqty;
    private BigDecimal totalBigBagqty;
    private BigDecimal totalWeight;
    private BigDecimal toatalVol;
    private Date createTime;
    private String createUser;
    private String blCode;

    @Data
    public static  class  BacklcListBussinessRecord{
        /**退库明细id*/
        private Long bllId;
        /**单头id*/
        private Long blId;
        /**出库任务明细id*/
        private Long dolId;
        /**出库任务id*/
        private Long dohId;
        /**产品id*/
        private Long productId;
        /**sku编号*/
        private String productNo;
        /**产品名称*/
        private String productName;
        /**条码*/
        private String productBarcode;
        /**图片*/
        private String mediaId;
        /**品牌*/
        private String brand;
        /**重量(大包)*/
        private BigDecimal bigBagWeight;
        /**体积(大包)*/
        private BigDecimal bigBagVol;
        /**箱规*/
        private BigDecimal boxQty;
        /**大包数量(件数)*/
        private BigDecimal bigBagQty;
        /**中包数量*/
        private BigDecimal midBagQty;
        /**大包转换率*/
        private BigDecimal bigBagRate;
        /**中包转换率*/
        private BigDecimal midBagRate;
        /**小包数量*/
        private BigDecimal smallBagQty;
        /**总数量-退库数(小包数)*/
        private BigDecimal bQty;
        /**退库重量(KG)*/
        private BigDecimal bWeight;
        /**退库体积(CBM)*/
        private BigDecimal bVol;
        /**样品库位*/
        private String lcCode;
    }

}
