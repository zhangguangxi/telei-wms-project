package com.telei.wms.datasource.wms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: leo
 * @date: 2020/9/10 15:31
 */
@Data
public class WmsAdjustHeaderPageQueryResponseVo {
    /**调整时间*/
    private Date createTime;
    /**单据编号*/
    private String adjhCode;
    /**调整类型 MOVE	移位，INCREASE 调增，REDUCE	调减，LIFTUP 升任务，LIFTDOWN 降任务 */
    private String adjhType;
    /**商品码*/
    private String productNo;
    /**商品名称*/
    private String productName;
    /**条码*/
    private String productBarcode;
    /**调整前数量*/
    private BigDecimal ivQty;
    /**调整前位置*/
    private String lcCode;
    /**调整数*/
    private BigDecimal ivQtyAdjt;
    /**调整后位置*/
    private String lcCodeAdjt;
    /**调整后数量*/
    private String ivQtyAfter;
    /**备注(reason)*/
    private String reason;
    /**
     * 调增：调增、盘盈
     * 调减：调减、盘亏、销毁、报损
     * */
    private String adjhDetailType;
    /** 中包转换数 */
    private Integer midBagQty;
    /** 大包转换数 */
    private Integer bigBagQty;
}
