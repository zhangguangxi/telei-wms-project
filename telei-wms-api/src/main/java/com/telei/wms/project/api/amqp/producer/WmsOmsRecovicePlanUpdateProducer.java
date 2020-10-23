package com.telei.wms.project.api.amqp.producer;

import com.telei.wms.project.api.amqp.producer.base.BaseProducer;
import org.springframework.stereotype.Component;

/**
 * @Auther: Dean
 * @Date: 2020/10/19 14:35
 */
@Component
public class WmsOmsRecovicePlanUpdateProducer extends BaseProducer {

    //队列名称
    public static final String QUEUE_NAME = "WMS-OMS-RecovicePlanUpdate";

    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
}
