package com.telei.wms.commons.amqp;

/**
 * @Auther: Dean
 * @Date: 2020/7/16 17:30
 */
public class Key {

    /**
     * 获取队列名
     * @param serverName
     * @param tableName
     * @return
     */
    public static String getQueueName(String serverName, String tableName) {
        return serverName + "-" + tableName;
    }

    /**
     * 获取serverName对应的system key
     * @param serverName
     * @param system
     * @return
     */
    public static String getServerKey(String serverName, String system) {
        return serverName + "-" + system;
    }

    /**
     * 获取serverName
     * @param queueName
     * @return
     */
    public static String getServerName(String queueName) {
        return queueName.split("-")[0];
    }
}
