package com.telei.wms.project.api.endpoint.inventory.strategy;

import lombok.Getter;

import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/8 16:02
 */
public enum AdjustEnum {
    INCREASE("INCREASE","调增","increaseAdjustStrategy"),
    REDUCE("REDUCE","调减","reduceAdjustStrategy"),
    MOVE("MOVE","移位","moveAdjustStrategy"),
    LIFTUP("LIFTUP","升任务","liftupAdjustStrategy"),
    LIFTDOWN("LIFTDOWN","降任务","liftdownAdjustStrategy"),

    ;
    /**调整类型*/
    @Getter
    private String adjustType;
    /**调整描述*/
    @Getter
    private String description;
    /**策略实现对应的beanName*/
    @Getter
    private String beanName;

    AdjustEnum(String adjustType, String description, String beanName) {
        this.adjustType = adjustType;
        this.description = description;
        this.beanName = beanName;
    }

    /***
     * 通过库存调整类型获取库存调整枚举
     * @param adjustType
     * @return
     */
    public static AdjustEnum findAdjustEnumByAdjustType(String adjustType){
        for (AdjustEnum adjustEnum : values()) {
            if(Objects.equals(adjustEnum.getAdjustType(),adjustType)){
                return adjustEnum;
            }
        }
        return null;
    }
}
