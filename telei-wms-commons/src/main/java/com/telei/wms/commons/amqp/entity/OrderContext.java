package com.telei.wms.commons.amqp.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: Dean
 * @Date: 2020/8/6 16:10
 */
@Data
public class OrderContext implements Serializable {

    private String className;
    private String methodName;
    private List<ParamType> paramTypes;
    private Object[] body;
}
