package com.davinvicky.service.test.util.mock;

import javax.jms.JMSException;
import javax.jms.Queue;

/**
 * Created by ryanda on 22/04/2016.
 */
public class MockQueue implements Queue {
    public String getQueueName() throws JMSException {
        return "EFM_MFP.eSchedule.Submit";
    }
}
