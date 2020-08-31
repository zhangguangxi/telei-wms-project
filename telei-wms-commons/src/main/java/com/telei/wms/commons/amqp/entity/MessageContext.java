package com.telei.wms.commons.amqp.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: Dean
 * @Date: 2020/7/9 16:10
 */
@Data
public class MessageContext implements Serializable {

    private String serverName;
    private String queueName;
    private String system;
    private Long id;
    private String cmdType;
    private String body;
}
