package com.telei.wms.project.api.amqp.consumer.base;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.telei.wms.commons.amqp.entity.MessageContext;
import com.telei.wms.commons.amqp.http.ApiResponse;
import com.telei.wms.commons.amqp.http.HttpClient;
import com.telei.wms.commons.utils.StringUtils;
import com.telei.wms.project.api.configuration.WmsIdInstantdirectiveConfig;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdExceptionAddRequest;
import com.telei.wms.project.api.endpoint.wmsIdInstantdirective.dto.WmsIdInstantdirectiveCallbackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * @Auther: Dean
 * @Date: 2020/7/27 15:35
 */
public abstract class BaseConsumer implements AbstractBaseConsumer {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WmsIdInstantdirectiveConfig idInstantdirectiveConfig;

    /**
     * 数据交互处理
     * @param messageContext
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(MessageContext messageContext, Channel channel, Message message) throws Exception {
        logger.debug("进入数据交互处理。" + "messageContext:" + JSON.toJSONString(messageContext));
        WmsIdInstantdirectiveCallbackRequest callbackRequest = new WmsIdInstantdirectiveCallbackRequest();
        try {
            //业务处理
            bussinessHandle(JSON.parse(messageContext.getBody()));
            //回调
            callback(messageContext, callbackRequest);
        } catch (Exception e) {
            logger.error(e.getClass().getName(), e);
            WmsIdExceptionAddRequest idExceptionAddRequest = new WmsIdExceptionAddRequest();
            idExceptionAddRequest.setIdId(messageContext.getId());
            idExceptionAddRequest.setExceptionNote(getStackTraceByString(e));
            callbackRequest.setIdExceptionAddRequest(idExceptionAddRequest);
            //回调
            callback(messageContext, callbackRequest);
            throw e;
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        logger.debug("数据交互处理成功");
    }

    /**
     * 把处理结果回调给消息生产方
     * @param messageContext
     * @param callbackRequest
     */
    public void callback(MessageContext messageContext, WmsIdInstantdirectiveCallbackRequest callbackRequest) {
        logger.debug("进入回调处理");
        String serverAddress = idInstantdirectiveConfig.getNodes().get(messageContext.getSystem());
        if (StringUtils.isEmpty(serverAddress)) {
            throw new RuntimeException("serverAddress不存在");
        }
        callbackRequest.setId(messageContext.getId());
        //发起回调
        ApiResponse response = HttpClient.post(serverAddress, callbackRequest);
        if (Objects.isNull(response.getData()) || ! response.getData().getIsSuccess()) {
            logger.error("response:" + JSON.toJSONString(response));
            throw new RuntimeException("回调处理失败");
        }
        logger.debug("回调处理成功");
    }

    /**
     * 获取异常堆栈信息
     * @param e
     * @return
     */
    private String getStackTraceByString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
