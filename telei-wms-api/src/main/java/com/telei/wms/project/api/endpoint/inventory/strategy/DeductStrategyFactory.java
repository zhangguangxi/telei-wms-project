package com.telei.wms.project.api.endpoint.inventory.strategy;

import com.nuochen.framework.component.commons.spring.SpringApplicationContext;
import com.telei.infrastructure.component.idgenerator.contract.Id;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author: leo
 * @date: 2020/9/8 16:24
 */
@Slf4j
@Component("deductStrategyFactory")
public class DeductStrategyFactory {
    @Autowired
    private Id idGenerator;


    /**
     * 通过出库扣减类型获取其策略的实现
     *
     * @param deductType
     * @return
     */
    public  IDeductStrategy getDeductStrategy(String deductType){
        DeductEnum deductEnum = DeductEnum.findDeductEnumByDeductType(deductType);
        if(Objects.isNull(deductEnum)){
            return null;
        }
        return  SpringApplicationContext.getBean(deductEnum.getBeanName(), IDeductStrategy.class);
    }
}
