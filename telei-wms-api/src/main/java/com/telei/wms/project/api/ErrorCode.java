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
    DO_ERROR_4002,
    DO_ERROR_4003,

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
     * 库存初始化单
     */
    INIT_NOT_EXIST_4001,
    INIT_ADD_ERROR_4002,
    INIT_AUDIT_ERROR_4003,
    INIT_DELETE_ERROR_4004,


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
    INVENTORY_ADD_ERROR_LC_CODE_IS_NULL_4022,
    INVENTORY_ADD_ERROR_4023,
    INVENTORY_ADD_ERROR_IV_QTY_IS_NULL_4023,
    INVENTORY_DETAIL_ERROR_4024,
    INVENTORY_ADD_ERROR_PAO_NOT_EXIST_40025,
    INVENTORY_DEDUCT_DO_RECORD_NOT_EXIST_40026,
    INVENTORY_DEDUCT_NOT_HAS_PLO_40027,
    INVENTORY_DEDUCT_NOT_HAD_CHECK_40028,
    INVENTORY_DEDUCT_DO_LINE_NOT_EXIST_40029,
    INVENTORY_DEDUCT_IV_OUT_NOT_EXIST_40030,
    INVENTORY_DEDUCT_PLO_LINE_NOT_EXIST_40031,
    INVENTORY_DEDUCT_INVENTORY_NOT_EXIST_40032,
    INVENTORY_DEDUCT_PRODUCT_NOT_EXIST_40033,
    INVENTORY_DEDUCT_PRODUCT_NOT_EXIST_40034,
    INVENTORY_DEDUCT_DELETE_IV_OUT_40035,
    INVENTORY_DEDUCT_ADD_OUT_CONFIRM_40036,
    INVENTORY_DEDUCT_ADD_TRANSACTION_40037,
    INVENTORY_DEDUCT_UPDATE_INVENTORY_40038,
    INVENTORY_DEDUCT_DELETE_INVENTORY_40039,
    INVENTORY_DEDUCT_UPDATE_DO_HEADER_40040,
    INVENTORY_ERROR_PRODUCT_NOT_EXIST_40041,
    INVENTORY_ERROR_PRODUCT_IS_MULTIPLE_40042,
    INVENTORY_ADD_ERROR_PAO_LINE_ALREADY_SHIFT_23,
    INVENTORY_ADD_ERROR_LC_CODE_NOT_EXIST_24,
    INVENTORY_MULTI_SAMPLE_LC_CODE_CHECK_40045,
    INVENTORY_MULTI_SAMPLE_LC_CODE_CHECK_40046,
    INVENTORY_MULTI_SAMPLE_LC_CODE_CHECK_40047,
    INVENTORY_DEDUCT_PLO_HEADER_IS_NULL_40048,
    INVENTORY_DEDUCT_PLO_NOT_FINISHED_40049,
    INVENTORY_DEDUCT_NONE_SAMPLE_LC_CODE_40050,
    INVENTORY_DEDUCT_NOT_EXIST_PRODUCT_LC_CODE_MAP_40051,
    INVENTORY_DEDUCT_INVENTORY_NOT_ENOUGH_40052,
    INVENTORY_DEDUCT_UPDATE_DO_LINE_40053,
    INVENTORY_DEDUCT_COMPANY_DEFAULT_WAREHOUSE_NOT_EXIST_40054,
    INVENTORY_DEDUCT_EXIST_PROCESS_PRODUCT_40055,
    INVENTORY_DEDUCT_CONTAINER_IS_NULL_40056,
    INVENTORY_ADD_ERROR_EXIST_MULTI_SAMPLE_LC_CODE_4057,






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
    ADJT_ERROR_4018,
    ADJT_ERROR_4019,
    ADJT_ERROR_4020,
    ADJT_ERROR_4021,


    /**
     * 上架单
     */
    PAO_NOT_EXIST_4001,
    PAO_ADD_ERROR_4002,
    PAO_CANCEL_ERROR_4003,
    PAO_NOT_EXIST_4004,

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
    PLO_ADD_ERROR_4010,
    PLO_ADD_ERROR_4011,
    PLO_CANCEL_ERROR_4012,
    PLO_CANCEL_ERROR_4013,
    PLO_CANCEL_ERROR_4014,
    PLO_CANCEL_ERROR_4015,
    PLO_ADD_ERROR_4016,
    PLO_ADD_ERROR_4017,
    PLO_CANCEL_ERROR_4018,
    PLO_ADD_ERROR_4019,

    LIFT_WORK_NOT_EXIST_4001,
    LIFT_WORK_ADD_ERROR_4002,
    LIFT_WORK_REVOKE_ERROR_4003,
    LIFT_WORK_UPDATE_ERROR_4004,
    LIFT_WORK_PREP_LC_CODE_IS_NULL_4005,
    LIFT_WORK_LC_CODE_NOT_EXIST_4006,
    LIFT_WORK_LC_CODE_IS_NOT_NULL_4007,
    LIFT_WORK_IS_UPDATE_4008,
    LIFT_WORK_PRODUCT_IS_NULL_EQUALS_4009,
    LIFT_WORK_EXIST_4010,
    LIFT_WORK_REVOKE_ERROR_4011,

    LIFT_TASK_UPDATE_ERROR_4001,
    LIFT_TASK_ADD_ERROR_4002,
    LIFT_TASK_COMMON_ERROR_4003,

    WMS_LOCATION_EXIST_4001,
    WMS_LOCATION_NOT_EXIST_4002,
    WMS_LOCATION_UPDATE_ERROR_4003,
    WMS_LOCATION_ADD_ERROR_4004,
    WMS_LOCATION_LC_CODE_EXIST_4005,
    WMS_LOCATION_DELETE_ERROR_4006,
    WMS_LOCATION_LIST_IS_NULL_4007,
    WMS_LOCATION_EXIST_PRODUCT_4008,
    WMS_LOCATION_EXIST_PRODUCT_BY_LOCODE_4009,

    /***
     *
     * 套打
     *
     */
    NEST_LIFT_WORK_PRINT_4001,
    NEST_CHECK_PRINT_4002,

    /**
     * 新品分配推荐库位
     */
    LC_RECOMMEND_NOT_EXIST_4001,
    LC_RECOMMEND_UPDATE_ERROR_4002,
    LC_RECOMMEND_CODE_IS_SAMPLE_4003,
    LC_RECOMMEND_NOT_EXIST_4004,

    /**
     * 装柜
     */
    DO_CONTAINER_QTY_IS_NULL_4001,
    DO_CONTAINER_QTY_IS_MAX_4002,


    /**
     *
     * 退库
     */
    BACK_LC_RECORD_IS_NULL_4001,
    BACK_LC_PRODUCT_ID_IS_NULL_4002,
    BACK_LC_LIST_ADD_FAILED_4003,
    BACK_LC_ADD_FAILED_4004,
    BACK_LC_UPDATE_FAILED_4005,
    BACK_LC_LIST_PRODUCT_NOT_EXIST_4007,
    BACK_LC_DO_HEADER_NOT_EXIST_4008,
    BACK_LC_ADD_FAILED_4009,
    BACK_LC_ADD_FAILED_4010,
    BACK_LC_ADD_FAILED_4011,
    BACK_LC_ADD_FAILED_4012,
    BACK_LC_ADD_FAILED_4013,
    BACK_LC_ADD_FAILED_4014,
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
