package com.telei.wms.project.api.amqp.producer;

import com.telei.wms.project.api.amqp.producer.base.BaseProducer;
import org.springframework.stereotype.Component;

/**
 * @author: leo
 * @date: 2020/9/1 15:10
 */
@Component
public class WmsOmsInventoryAddWriteBackProducer extends BaseProducer {
    //队列名称
    public static final String QUEUE_NAME = "WMS-OMS-InventoryAddWriteBack";

    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }
}
