package com.telei.wms.project.api.amqp.producer.base;

import com.alibaba.fastjson.JSON;
import com.telei.wms.commons.amqp.entity.MessageContext;
import com.telei.wms.datasource.wms.model.WmsIdInstantdirective;
import com.telei.wms.project.api.configuration.WmsIdInstantdirectiveConfig;
import com.telei.wms.project.api.utils.DataConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Auther: Dean
 * @Date: 2020/7/27 17:35
 */
public abstract class BaseProducer implements AbstractBaseProducer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WmsIdInstantdirectiveConfig idInstantdirectiveConfig;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送消息到MQ
     * @param object
     */
    public void send(Object object) {
        WmsIdInstantdirective idInstantdirective = DataConvertUtil.parseDataAsObject(object, WmsIdInstantdirective.class);
        //组装MessageContext
        MessageContext messageContext = new MessageContext();
        messageContext.setSystem(idInstantdirectiveConfig.getSystem());
        messageContext.setId(idInstantdirective.getId());
        messageContext.setCmdType(idInstantdirective.getIdtCode());
        messageContext.setBody(idInstantdirective.getOrderContext());
        logger.debug("send：" + JSON.toJSONString(messageContext));
        try {
            amqpTemplate.convertAndSend(getQueueName(), JSON.toJSONString(messageContext));
        } catch (Throwable t) {
            logger.error(t.getClass().getName(), t);
        }
    }
}
