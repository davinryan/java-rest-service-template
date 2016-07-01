package com.davinvicky.service.dao;

import javax.jms.JMSException;

/**
 * Basic dao for sending jms messages
 */
public interface JMSDao {

    /**
     * This method will check MQ is still alive.
     */
    void healthCheck() throws JMSException;

    /**
     * Send Invoice to MQ server.
     *
     * @param textMessage document to send.
     */
    void send(String textMessage) throws JMSException;
}
