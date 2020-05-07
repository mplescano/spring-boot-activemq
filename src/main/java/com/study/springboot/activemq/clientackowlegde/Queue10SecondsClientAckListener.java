package com.study.springboot.activemq.clientackowlegde;

import com.study.springboot.activemq.ActiveMQSender;
import com.study.springboot.activemq.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
public class Queue10SecondsClientAckListener {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQSender.class);

    @JmsListener(destination = Queues.QUEUE_REDELIVERY_EVERY_10_SECONDS_ACK,
            concurrency = "10-10", subscription = "teste2",
            containerFactory = "jmsACKListenerContainerFactory",
            id = "padrao2")
    public void onMessage(Message message,
                          Session session,
                          @Header(name = "JMSXDeliveryCount", defaultValue = "1") String redeliveryCount,
                          @Header(name = JmsHeaders.MESSAGE_ID, defaultValue = "1") String messageId) throws JMSException {
        try {
            String msg = ((TextMessage) message).getText();
            logger.info("Receiving message {} from queue {} [RedeliveryCount={}, MessageID={}]", msg, Queues.QUEUE_REDELIVERY_EVERY_10_SECONDS_ACK, redeliveryCount, messageId);

            if (message != null && msg.startsWith("error-")) {
                throw new Exception("Forcing reading jms message problemas for message read in queue " + Queues.QUEUE_REDELIVERY_EVERY_10_SECONDS_ACK);
            }

            message.acknowledge();
        } catch (Exception e) {
            logger.error("Problems for consuming messagem from queue {}.", Queues.QUEUE_REDELIVERY_EVERY_10_SECONDS_ACK);
            session.recover();
        }
    }
}