package com.paic.arch.jmsbroker.construt;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 会话工厂类
 * @author HeGuanXun
 */
public class JmsMessageBroker {

    private static final Logger logger = getLogger(JmsMessageBroker.class);

    private static JmsMessageBroker jmsMessageBroker;

    private BrokerService brokerService;

    private  String brokerUrl;

    private  Connection connection;
    /**mq会话*/
    private  Session session;

   /**
    *@Author:create by HeGuanXun
    *@Description:获取ActiveMq的回话ssesion
    *@date:18:09 2018/3/6
    */
    protected Session getJmsSession(){
        //启动连接
        try {
            connection = new ActiveMQConnectionFactory(brokerUrl).createConnection();
            connection.start();
        } catch (JMSException e) {
            logger.error("failed to create connection to {}");
            e.printStackTrace();
        }
        //创建session
        try {
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            logger.error("Failed to create session on connection {}", connection);
            e.printStackTrace();
        }
        return session;
    }

    /**
     * 关闭会话跟连接
     */
    protected void closeSession() {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException jmse) {
                logger.warn("Failed to close session");
                throw new IllegalStateException(jmse);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException jmse) {
                logger.warn("Failed to close connection to broker");
                throw new IllegalStateException(jmse);
            }
        }
    }

    protected static JmsMessageBroker getInstance(){
         if (jmsMessageBroker==null){
             jmsMessageBroker = new JmsMessageBroker();
         }
        return jmsMessageBroker;
    }

    public BrokerService getBrokerService() {
        return brokerService;
    }

    public void setBrokerService(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }
}
