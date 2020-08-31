package com.telei.wms.project.api.amqp.producer.base;

/**
 * @Auther: Dean
 * @Date: 2020/7/28 9:35
 */
public interface AbstractBaseProducer {

    /**
     * 队列名称
     * @return
     */
    String getQueueName();

    /**
     * 发送消息到MQ
     * @param object
     */
    void send(Object object);
}
