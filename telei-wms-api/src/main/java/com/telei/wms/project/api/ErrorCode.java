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
