package com.telei.wms.project.api.endpoint.inventory.strategy;

import lombok.Getter;

import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/8 16:02
 */
public enum DeductEnum {
    LOAD_CONTANER("Y","装柜","loadContainerStrategy"),
    UNLOAD_CONTANER("N","不装柜","unloadContainerStrategy"),

    ;
    /**扣减类型*/
    @Getter
    private String deductType;
    /**调整描述*/
    @Getter
    private String description;
    /**策略实现对应的beanName*/
    @Getter
    private String beanName;

    DeductEnum(String deductType, String description, String beanName) {
        this.deductType = deductType;
        this.description = description;
        this.beanName = beanName;
    }

    /***
     * 通过扣减类型获取扣减枚举
     * @param deductType
     * @return
     */
    public static DeductEnum findDeductEnumByDeductType(String deductType){
        for (DeductEnum adjustEnum : values()) {
            if(Objects.equals(adjustEnum.getDeductType(),deductType)){
                return adjustEnum;
            }
        }
        return null;
    }
}
