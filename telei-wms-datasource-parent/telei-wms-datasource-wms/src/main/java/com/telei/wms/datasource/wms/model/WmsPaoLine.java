package com.telei.wms.datasource.wms.model;

import com.nuochen.framework.autocoding.domain.Entity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
* wms_pao_line ä¸Šæž¶å�•æ˜Žç»†
*/
@Data
public class WmsPaoLine implements Entity<Long> {
    /** id */
    private Long paolId;
    /** å�•å¤´id */
    private Long paoId;
    /** å…¥åº“å�•å�•å¤´id */
    private Long roId;
    /** å¯¹åº”è®¡åˆ’æ˜Žç»†çš„id */
    private Long rolId;
    /** æ”¶è´§ä½œå�•å¤´id */
    private Long rooId;
    /** æ”¶è´§å�•å�· */
    private String rooCode;
    /** æ”¶è´§å�•æ”¶è´§æ˜Žç»†id */
    private Long roolId;
    /** äº§å“�id */
    private Long productId;
    /** å�•ä½�æ¯›é‡�(KG) */
    private BigDecimal unitGrossWeight;
    /** å�•ä½�ä½“ç§¯(CBM) */
    private BigDecimal unitVol;
    /** å¤‡æ³¨ */
    private String memo;
    /** ä¸Šæž¶æ•°é‡� */
    private BigDecimal paolQty;
    /** è®¡é‡�å�•ä½� */
    private Integer stockUnit;
    /** æ˜Žç»†è¡Œæ€»é‡�é‡�(KG) */
    private BigDecimal lineTotalWeight;
    /** æ˜Žç»†è¡Œå‡€é‡�(KG) */
    private BigDecimal lineNetWeight;
    /** æ˜Žç»†è¡Œæ€»ä½“ç§¯(CBM) */
    private BigDecimal lineTotalVol;
    /** ä¸Šæž¶çŠ¶æ€�  01-åˆ¶å�•ï¼Œ20-å·²ä¸Šæž¶ */
    private String paoStatus;
    /** åˆ›å»ºç”¨æˆ· */
    private String createUser;
    /** åˆ›å»ºæ—¶é—´ */
    private Date createTime;
    /** åº“å­˜æ‰¹æ¬¡id */
    private Long iabId;
    /** æŽ¨è��åº“ä½� */
    private String prepLcCode;
    /** ä¸Šæž¶åº“ä½� */
    private String lcCode;
    /** å…ˆè¿›å…ˆå‡ºæ—¶é—´ */
    private Date paolFifoTime;
    /** ä¸Šæž¶æ—¶é—´ */
    private Date putawayTime;
    /** ä¸Šæž¶ç”¨æˆ· */
    private String putawayUser;
    @Override
    public Long getId() {
        return paolId;
    }
    @Override
    public void setId(Long id) {
        this.paolId = id;
    }
}