package com.telei.wms.project.api.amqp.producer;

import com.telei.wms.project.api.amqp.producer.base.BaseProducer;
import org.springframework.stereotype.Component;

/**
 * @Auther: Dean
 * @Date: 2020/8/26 15:35
 */
@Component
public class WmsOmsSaleRmaCallbackProducer extends BaseProducer {

    //队列名称
    public static final String QUEUE_NAME = "WMS-OMS-SaleRmaCancelCallback";

    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
}
