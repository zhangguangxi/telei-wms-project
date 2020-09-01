package com.telei.wms.project.api;


import com.nuochen.framework.component.validation.ValidationException;

/**
 * @Description: 错误码(相同功能前置相同编号递增,编号都从4001开始)
 * @Auther: leo
 * @Date: 2020/6/8 15:53
 */
public enum ErrorCode {
    /**商品新增错误-id错误(示例)*/
        GOODS_ADD_ERROR_4001,

    /**
     * 入库任务
     */
    RO_NOT_EXIST_4001,

    /**
     * 业务单据
     */
    BUSINESS_NUMBER_ERROR_4001,

    /**
     * 收货单
     */
    ROO_NOT_EXIST_4001,
    ROO_ADD_ERROR_4002,
    ROO_REVOKE_ERROR_4003,
    ROO_LINE_NOT_EXIST_4004,

    /**
     * 收货单
     */
    INIT_NOT_EXIST_4001,
    INIT_ADD_ERROR_4002,
    INIT_AUDIT_ERROR_4003,


    /**
     * 库存
     */
    INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001,//上架失败-上架记录为空
    INVENTORY_ADD_ERROR_PRODUCT_ID_IS_NULL_4002,//上架失败-产品ID为空
    INVENTORY_ADD_ERROR_PRODUCT_NOT_EXIST_4003,//上架失败-产品记录不存在
    INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_ID_IS_NULL_4004,//上架失败-库存批次ID为空
    INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_NOT_EXIST_4005,//上架失败-库存批次记录不存在
    INVENTORY_ADD_ERROR_PAO_LINE_ID_IS_NULL_4006,//上架失败-上架单明细ID为空
    INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007, //上架失败-收货单id为空
    INVENTORY_ADD_ERROR_ROO_NOT_EXIST_4008, //上架失败-收货单单头不存在
    INVENTORY_ADD_ERROR_RO_ID_IS_NULL_4010, //上架失败-入库任务单头id为空
    INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011, //上架失败-入库任务单头记录不存在
    INVENTORY_ADD_ERROR_ROL_ID_IS_NULL_4012, //上架失败-入库计划id为空
    INVENTORY_ADD_ERROR_ROL_LINE_RECORD_NOT_EXIST_4013, //上架失败-入库计划记录不存在
    INVENTORY_ADD_ERROR_4014, //上架失败-库存记录插入失败
    INVENTORY_ADD_ERROR_4015, //上架失败-历史库存记录插入失败
    INVENTORY_ADD_ERROR_4016, //上架失败-更新上架单单头失败
    INVENTORY_ADD_ERROR_4017, //上架失败-更新上架单明细失败
    INVENTORY_ADD_ERROR_4018, //上架失败-更新收货单单头
    INVENTORY_ADD_ERROR_4019, //上架失败-更新入库单单头
    INVENTORY_ADD_ERROR_PAO_ID_IS_NULL_4020,//上架失败-上架单单头ID为空
    INVENTORY_ADD_ERROR_ROOL_ID_IS_NULL_4021, //上架失败-收货单明细ID为空
    INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4021, //上架失败-收货单单头ID不为空

    /**
     * 上架单
     */
    PAO_NOT_EXIST_4001,
    ;

    private  String code;
    private String messageKey;

    ErrorCode() {
        final String name = this.name();
        final String[] items = name.split(",");
        this.code= items[items.length -1];
        this.messageKey = name;
    }

    public void throwError(Object... arguments){
        throw getError(arguments);
    }

    public ValidationException getError(Object... arguments) {
        return new ValidationException(this.code,this.messageKey,arguments);
    }
}
