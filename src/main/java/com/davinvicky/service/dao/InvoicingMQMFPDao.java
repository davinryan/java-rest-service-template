package com.davinvicky.service.dao;

import com.davinvicky.common.jms.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * Main class for sending jms message to MQ queue for MFP.
 */
@Component
public class InvoicingMQMFPDao implements JMSDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoicingMQMFPDao.class.getName());

    @Autowired
    private MessagePublisher publisher;

    @Value("#{'${invoicing.jms.mfp.sendingAppPropertyName:Not Set}'}")
    private String sendingAppPropertyName;

    @Value("#{'${invoicing.jms.mfp.sendingAppPropertyValue:Not Set}'}")
    private String sendingAppPropertyValue;

    @Value("#{'${invoicing.jms.mfp.messageType}'}")
    private String messageType;

    /** {@inheritDoc}.*/
    @Override
    public void healthCheck() throws JMSException {
        LOGGER.info("#####################################");
        LOGGER.info("Performing MQ HealthCheck");
        LOGGER.info("#####################################");
        try {
            publisher.healthCheck();
        } catch (JMSException e) {
            LOGGER.error("Failed to check that MQ was alive", e);
            throw e;
        }
    }

    /** {@inheritDoc}.*/
    @Override
    public void send(final String xmlDocument) throws JMSException {
        try {
            publisher.send(xmlDocument, messageType, sendingAppPropertyName, sendingAppPropertyValue);
        } catch (JMSException e) {
            LOGGER.error("Failed to send jms message", e);
            throw e;
        }
    }
}
