package com.davinryan.service.dao;

import com.davinryan.common.restservice.jms.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * Main class for sending jms message to MQ queue for.
 */
@Component
public class ExampleMQDao implements JMSDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleMQDao.class.getName());

    @Autowired
    private MessagePublisher publisher;

    @Value("#{'${invoicing.jms.sendingAppPropertyName:Not Set}'}")
    private String sendingAppPropertyName;

    @Value("#{'${invoicing.jms.sendingAppPropertyValue:Not Set}'}")
    private String sendingAppPropertyValue;

    @Value("#{'${invoicing.jms.messageType}'}")
    private String messageType;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void healthCheck() throws JMSException {
        LOGGER.info("#####################################");
        LOGGER.info("Performing HealthCheck");
        LOGGER.info("#####################################");
        publisher.healthCheck();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void send(final String xmlDocument) throws JMSException {
        publisher.send(xmlDocument, messageType, sendingAppPropertyName, sendingAppPropertyValue);
    }
}
