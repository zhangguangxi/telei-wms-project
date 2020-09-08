package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.nuochen.framework.component.commons.spring.SpringApplicationContext;

import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/8 16:24
 */
public class AdjustStrategyFactory {
    /**
     * 通过库存调整类型获取其策略的实现
     *
     * @param adjustType
     * @return
     */
    public static IAdjustStrategy getAdjustStrategy(String adjustType){
        AdjustEnum adjustEnum = AdjustEnum.findAdjustEnumByadjustType(adjustType);
        if(Objects.isNull(adjustEnum)){
            return null;
        }
        return  SpringApplicationContext.getBean(adjustEnum.getBeanName(), IAdjustStrategy.class);
    }
}
