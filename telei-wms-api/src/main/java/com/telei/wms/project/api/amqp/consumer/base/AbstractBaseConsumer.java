package com.telei.wms.project.api.amqp.consumer.base;

import com.rabbitmq.client.Channel;
import com.telei.wms.commons.amqp.entity.MessageContext;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdInstantdirectiveCallbackRequest;
import org.springframework.amqp.core.Message;

/**
 * @Auther: Dean
 * @Date: 2020/7/27 9:35
 */
public interface AbstractBaseConsumer {

    /**
     * Consumer对应的业务处理
     * @param object
     */
    void bussinessHandle(Object object);

    /**
     * 处理数据交互
     * @param messageContext
     * @param channel
     * @param message
     * @throws Exception
     */
    void handle(MessageContext messageContext, Channel channel, Message message) throws Exception;

    /**
     * 处理回调
     * @param messageContext
     * @param callbackRequest
     */
    void callback(MessageContext messageContext, WmsIdInstantdirectiveCallbackRequest callbackRequest);
}
