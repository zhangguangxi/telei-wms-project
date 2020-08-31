package com.telei.wms.project.api.amqp.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.telei.wms.commons.amqp.entity.MessageContext;
import com.telei.wms.project.api.amqp.consumer.base.BaseConsumer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @Auther: Dean
 * @Date: 2020/7/28 10:25
 */
//@RabbitListener(
//    bindings = @QueueBinding(
//        value = @Queue(TestConsumer.QUEUE_NAME),
//        exchange = @Exchange("defaultExchange")
//    )
//)
@Component
public class TestConsumer extends BaseConsumer {

    //队列名称
    public static final String QUEUE_NAME = "WMS-CDM-test";

    @RabbitHandler
    public void process(String messageContext, Channel channel, Message message) throws Exception {
        //数据交互处理
        handle(JSON.parseObject(messageContext, MessageContext.class), channel, message);
    }

    @Override
    public void bussinessHandle(Object object) {
        System.out.println(JSON.toJSONString(object));
    }
}
