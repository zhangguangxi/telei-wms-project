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
    RO_ENFORCEMENT_ERROR_4002,
    RO_ENFORCEMENT_ERROR_4003,

    /**
     * 出库任务
     */
    DO_NOT_EXIST_4001,

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
    INVENTORY_ADD_ERROR_PAO_LINE_RECORD_NOT_EXIST_4001,
    INVENTORY_ADD_ERROR_PRODUCT_ID_IS_NULL_4002,
    INVENTORY_ADD_ERROR_PRODUCT_NOT_EXIST_4003,
    INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_ID_IS_NULL_4004,
    INVENTORY_ADD_ERROR_INVENTORY_ATTRIBUTE_BATCH_NOT_EXIST_4005,
    INVENTORY_ADD_ERROR_PAO_LINE_ID_IS_NULL_4006,
    INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4007,
    INVENTORY_ADD_ERROR_ROO_NOT_EXIST_4008,
    INVENTORY_ADD_ERROR_RO_ID_IS_NULL_4010,
    INVENTORY_ADD_ERROR_RO_NOT_EXIST_4011,
    INVENTORY_ADD_ERROR_ROL_ID_IS_NULL_4012,
    INVENTORY_ADD_ERROR_ROL_LINE_RECORD_NOT_EXIST_4013,
    INVENTORY_ADD_ERROR_4014,
    INVENTORY_ADD_ERROR_4015,
    INVENTORY_ADD_ERROR_4016,
    INVENTORY_ADD_ERROR_4017,
    INVENTORY_ADD_ERROR_4018,
    INVENTORY_ADD_ERROR_4019,
    INVENTORY_ADD_ERROR_PAO_ID_IS_NULL_4020,
    INVENTORY_ADD_ERROR_ROOL_ID_IS_NULL_4021,
    INVENTORY_ADD_ERROR_ROO_ID_IS_NULL_4022,
    INVENTORY_ADD_ERROR_4023,
    INVENTORY_DETAIL_ERROR_4024,
    INVENTORY_ADD_ERROR_PAO_NOT_EXIST_40025,




    /***
     * 库存调整单
     */
    ADJT_ERROR_4001,
    ADJT_ERROR_4002,
    ADJT_ERROR_4003,
    ADJT_ERROR_4005,
    ADJT_ERROR_4006,
    ADJT_ERROR_4007,
    ADJT_ERROR_4008,
    ADJT_ERROR_4009,
    ADJT_ERROR_4010,
    ADJT_ERROR_4011,
    ADJT_ERROR_4012,
    ADJT_ERROR_4013,
    ADJT_ERROR_4014,
    ADJT_ERROR_4015,
    ADJT_ERROR_4016,
    ADJT_ERROR_4017,


    /**
     * 上架单
     */
    PAO_NOT_EXIST_4001,
    PAO_ADD_ERROR_4002,
    PAO_CANCEL_ERROR_4003,

    /**
     * 拣货单
     */
    PLO_NOT_EXIST_4001,
    PLO_NOT_EXIST_4002,
    PLO_ADD_ERROR_4003,
    PLO_ADD_ERROR_4004,
    PLO_ADD_DETAIL_ERROR_4005,
    PLO_FINISH_ERROR_4006,
    PLO_FINISH_ERROR_4007,
    PLO_FINISH_ERROR_4008,
    PLO_ADD_ERROR_4009,
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
