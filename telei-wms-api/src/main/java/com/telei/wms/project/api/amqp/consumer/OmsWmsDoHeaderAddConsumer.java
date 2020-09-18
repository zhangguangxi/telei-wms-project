package com.telei.wms.project.api.amqp.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.telei.wms.commons.amqp.entity.MessageContext;
import com.telei.wms.project.api.amqp.consumer.base.BaseConsumer;
import com.telei.wms.project.api.endpoint.doHeader.DoHeaderBussiness;
import com.telei.wms.project.api.endpoint.doHeader.dto.DoHeaderAddRequest;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: gongrp
 * @Date: 2020/9/17 10:35
 */
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue(OmsWmsDoHeaderAddConsumer.QUEUE_NAME),
        exchange = @Exchange("defaultExchange")
    )
)
@Component
public class OmsWmsDoHeaderAddConsumer extends BaseConsumer {

    //队列名称
    public static final String QUEUE_NAME = "OMS-WMS-DoHeaderAdd";

    @Autowired
    private DoHeaderBussiness doBussiness;

    @RabbitHandler
    public void process(String messageContext, Channel channel, Message message) throws Exception {
        //数据交互处理
        handle(JSON.parseObject(messageContext, MessageContext.class), channel, message);
    }

    @Override
    public void bussinessHandle(Object object) {
        DoHeaderAddRequest request = DataConvertUtil.parseDataAsObject(object, DoHeaderAddRequest.class);
        doBussiness.addDo(request);
    }
}
