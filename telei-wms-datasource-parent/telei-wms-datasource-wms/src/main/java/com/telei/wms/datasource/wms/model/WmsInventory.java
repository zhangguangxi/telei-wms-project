package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
* wms_inventory åº“å­˜è¡¨
*/
@Data
public class WmsInventory implements Entity<Long> {
    /** id */
    private Long ivId;
    /** å…¬å�¸id */
    private Long companyId;
    /** ä»“åº“id */
    private Long warehouseId;
    /** ä»“åº“code */
    private String warehouseCode;
    /** åº“ä½�ç¼–ç � */
    private String lcCode;
    /** å®¢æˆ·id */
    private Long customerId;
    /** äº§å“�åº�åˆ—ç � */
    private Long productId;
    /** åº“å­˜æ‰¹æ¬¡id */
    private Long iabId;
    /** å®žç‰©ç±»åˆ«ï¼ŒGDå�ˆæ ¼ BKç ´æ�Ÿ MDéœ‰å�˜ WEæ·‹æ¹¿ DFæ®‹æ¬¡ï¼Œæ•°æ�®å­—å…¸COMMON_QS_CODE */
    private String qsCode;
    /** å…ˆè¿›å…ˆå‡ºæ—¶é—´ */
    private Date ivFifoTime;
    /** åº“å­˜æ•°é‡� */
    private BigDecimal ivQty;
    /** è®¡é‡�å�•ä½� */
    private Long stockUnit;
    /** ä¸­åŒ…æ•°é‡� */
    private BigDecimal midBagQty;
    /** ä¸­åŒ…è½¬æ�¢æ•° */
    private BigDecimal midBagRate;
    /** ä¸­åŒ…å‰©ä½™æ•°é‡� */
    private BigDecimal midBagExtraQty;
    /** å¤§åŒ…æ•°é‡� */
    private BigDecimal bigBagQty;
    /** å¤§åŒ…è½¬æ�¢æ•° */
    private BigDecimal bigBagRate;
    /** å¤§åŒ…å‰©ä½™æ•°é‡� */
    private BigDecimal bigBagExtraQty;
    /** åº“å­˜é”�ï¼Œ0å�¯ä»¥å‡ºå‡ºè´§ï¼Œ1ä¸�å�¯ä»¥å‡ºè´§ */
    private Integer ivLocksign;
    /** å†»ç»“é”�ï¼Œ0å�¯ä»¥æ“�ä½œï¼Œ1ä¸�å�¯ä»¥ç§»åº“ã€�æ‹†åˆ†ã€�å‡ºè´§ã€�æ›´æ–° */
    private Integer ivFreezesign;
    /** ä¸šåŠ¡æ—¥æœŸ */
    private Date bizDate;
    /** åˆ›å»ºæ—¶é—´ */
    private Date ivCreatetime;
    /** æœ€è¿‘åº“å­˜æ›´æ–°æ—¶é—´ */
    private Date ivTranstime;
    /** åº”ç”¨ç±»åž‹ä»£ç �,å¼•èµ·æœ¬æ¬¡åº“å­˜å�˜åŠ¨çš„æ�¥æº�å�•æ�®çš„åº”ç”¨ç±»åž‹  RECV å…¥åº“å�•æ”¶è´§ DOMT å‡ºåº“è®¢å�• ADJT åº“å­˜è°ƒæ•´å�• ç­‰ */
    private String apCodeDc;
    /** å¼•èµ·åº“å­˜å�˜åŠ¨ä¸šåŠ¡å�•æ�®ç¼–å�· */
    private String ivDocumentCode;
    /** å¼•èµ·åº“å­˜å�˜åŠ¨å�•æ�®id */
    private Long ivDocumentId;
    /** å¼•èµ·åº“å­˜å�˜åŠ¨å�•æ�®æ˜Žç»†id */
    private Long ivDocumentlineId;
    /** ç”Ÿäº§æ—¥æœŸ */
    private Date productDate;
    /** æœ‰æ•ˆæ—¥æœŸ */
    private Date ivEffectiveDate;
    /** å®¢æˆ·æŒ‡å®šæ‰¹æ¬¡å�· */
    private String batchNo;
    /** åŽŸåº“å­˜id */
    private Long ivIdFrom;
    @Override
    public Long getId() {
        return ivId;
    }
    @Override
    public void setId(Long id) {
        this.ivId = id;
    }
}