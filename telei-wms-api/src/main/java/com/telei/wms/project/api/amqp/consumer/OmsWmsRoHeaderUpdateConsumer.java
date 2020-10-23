package com.telei.wms.project.api.amqp.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.telei.wms.commons.amqp.entity.MessageContext;
import com.telei.wms.project.api.amqp.consumer.base.BaseConsumer;
import com.telei.wms.project.api.endpoint.ro.RoBussiness;
import com.telei.wms.project.api.endpoint.ro.dto.RoHeaderUpdateRequest;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: Dean
 * @Date: 2020/10/19 11:30
 */
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue(OmsWmsRoHeaderUpdateConsumer.QUEUE_NAME),
        exchange = @Exchange("defaultExchange")
    )
)
@Component
public class OmsWmsRoHeaderUpdateConsumer extends BaseConsumer {

    //队列名称
    public static final String QUEUE_NAME = "OMS-WMS-RoHeaderUpdate";

    @Autowired
    private RoBussiness roBussiness;

    @RabbitHandler
    public void process(String messageContext, Channel channel, Message message) throws Exception {
        //数据交互处理
        handle(JSON.parseObject(messageContext, MessageContext.class), channel, message);
    }

    @Override
    public void bussinessHandle(Object object) {
        RoHeaderUpdateRequest request = DataConvertUtil.parseDataAsObject(object, RoHeaderUpdateRequest.class);
        roBussiness.updateRoHeader(request);
    }
}
