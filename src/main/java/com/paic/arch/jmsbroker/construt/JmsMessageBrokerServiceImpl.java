package com.paic.arch.jmsbroker.construt;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DestinationStatistics;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.lang.IllegalStateException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 服务类接口实现
 * @author HeGuanXun
 */
@Service
public class JmsMessageBrokerServiceImpl implements JmsMessageBrokerService {

    private static final Logger logger = getLogger(JmsMessageBrokerServiceImpl.class);

    private Session session;

    @Override
    public void sendMessage(String aDestinationName, String aMessage) {

        JmsMessageBroker jmsMessageBroker = JmsMessageBroker.getInstance();

        session = jmsMessageBroker.getJmsSession();

        try {
            //创建队列
            Queue queue = this.session.createQueue(aDestinationName);
            MessageProducer producer = this.session.createProducer(queue);
            //发送消息
            producer.send(this.session.createTextMessage(aMessage));
            producer.close();
        } catch (Exception e) {
            logger.warn("Failed to send message");
            throw new IllegalStateException(e.getMessage());
        } finally {
            jmsMessageBroker.closeSession();
        }
    }

    @Override
    public String getMessage(String aDestinationName, long aTime) {

        JmsMessageBroker jmsMessageBroker = JmsMessageBroker.getInstance();

        session = jmsMessageBroker.getJmsSession();

        String msg;
        try {
            Queue queue = session.createQueue(aDestinationName);
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive(aTime);
            consumer.close();
            if (message != null) {
                msg = ((TextMessage) message).getText();
            } else {
                throw new NoMessageReceivedException(
                        String.format("No messages received from the broker within the %d timeout", aTime));
            }
        } catch (JMSException jmse) {
            logger.warn("Failed to receive message");
            throw new IllegalStateException(jmse);
        } finally {
            jmsMessageBroker.closeSession();
        }
        return msg;
    }

    @Override
    public long getMessageCount(String aDestinationName) {
        long count = 0;
        try {
            count = getDestinationStatisticsFor(aDestinationName).getMessages().getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void startBroker() {
        JmsMessageBroker jmsMessageBroker = JmsMessageBroker.getInstance();
        try {
            jmsMessageBroker.setBrokerService(new BrokerService());
            jmsMessageBroker.getBrokerService().setPersistent(false);
            jmsMessageBroker.getBrokerService().addConnector(jmsMessageBroker.getBrokerUrl());
            jmsMessageBroker.getBrokerService().start();
        } catch (Exception e) {
            logger.warn("Failed to add Connector to broker");
            e.printStackTrace();
        }
    }

    @Override
    public void stopBroker() {
        JmsMessageBroker jmsBroker = JmsMessageBroker.getInstance();
        if (jmsBroker.getBrokerService() != null) {
            try {
                jmsBroker.getBrokerService().stop();
                jmsBroker.getBrokerService().waitUntilStopped();
            } catch (Exception e) {
                logger.warn("annot stop the broker");
                throw new IllegalStateException(e.getMessage());
            }
        }
    }

    private DestinationStatistics getDestinationStatisticsFor(String aDestinationName) throws Exception {

        JmsMessageBroker jmsBroker = JmsMessageBroker.getInstance();

        Broker regionBroker = jmsBroker.getBrokerService().getRegionBroker();

        for (org.apache.activemq.broker.region.Destination destination : regionBroker.getDestinationMap().values()) {

            if (destination.getName().equals(aDestinationName)) {
                return destination.getDestinationStatistics();
            }
        }
        throw new IllegalStateException(String.format("Destination %s does not exist on broker at %s", aDestinationName));
    }

    @Override
    public boolean isEmptyQueueAt(String aDestinationName) {
        long count = 0;
        try {
            count = getEnqueuedMessageCountAt(aDestinationName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count == 0;
    }

    public long getEnqueuedMessageCountAt(String aDestinationName) throws Exception {
        return getDestinationStatisticsFor(aDestinationName).getMessages().getCount();
    }

    @Override
    public void bindToBrokerAtUrl(String aBrokerUrl) {
        JmsMessageBroker.getInstance().setBrokerUrl(aBrokerUrl);
    }

    @Override
    public String getBrokerUrl() {
        return JmsMessageBroker.getInstance().getBrokerUrl();
    }

    @SuppressWarnings("serial")
    public class NoMessageReceivedException extends RuntimeException {
        public NoMessageReceivedException(String reason) {
            super(reason);
        }
    }
}
