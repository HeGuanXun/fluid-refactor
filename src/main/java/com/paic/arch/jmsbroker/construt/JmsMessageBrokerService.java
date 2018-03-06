package com.paic.arch.jmsbroker.construt;

/**
 *@Author:create by HeGuanXun
 *@Description:定义接口
 *@date:17:37 2018/3/6
 */
public interface JmsMessageBrokerService {

    /**
     *@Author:create by HeGuanXun
     *@Description:绑定broker
     *@param: * @param aBrokerUrl
     *@date:17:36 2018/3/6
     */
    public void bindToBrokerAtUrl(String aBrokerUrl);

    /**
     *@Author:create by HeGuanXun
     *@Description:获取url
     *@param: * @param null
     *@date:17:36 2018/3/6
     */
    public String getBrokerUrl();

    /**
     *@Author:create by LaoHe
     *@Description:消息发送
     *@param: * @param aDestinationName,aMessage
     *@date:17:36 2018/3/6
     */
    public void sendMessage(String aDestinationName, String aMessage);

    /**
     *@Author:create by HeGuanXun
     *@Description:获取消息
     *@param: * @param aDestinationName,aTime
     *@date:17:35 2018/3/6
     */
    public String getMessage(String aDestinationName, long aTime);

    /**
     *@Author:create by HeGuanXun
     *@Description:获取消息条数
     *@param: * @param aDestinationName
     *@date:17:35 2018/3/6
     */
    public long getMessageCount(String aDestinationName);

    /**
     *@Author:create by HeGuanXun
     *@Description:判断队列是否为空
     *@param: * @param aDestinationName
     *@date:17:35 2018/3/6
     */
    public boolean isEmptyQueueAt(String aDestinationName);

    /**
     *@Author:create by HeGuanXun
     *@Description:启动broker
     *@date:17:34 2018/3/6
     */
    public void startBroker();

    /**
     *@Author:create by HeGuanXun
     *@Description:停止broker
     *@param: * @param null
     *@date:17:33 2018/3/6
     */
    public void stopBroker();

}
