package nz.co.acc.myacc.services.test.util.mock;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

/**
 * Created by ryanda on 22/04/2016.
 */
public class MockQueueConnectionFactory implements QueueConnectionFactory {
    public QueueConnection createQueueConnection() throws JMSException {
        return new MockQueueConnection();
    }

    public QueueConnection createQueueConnection(String s, String s1) throws JMSException {
        return new MockQueueConnection();
    }

    public Connection createConnection() throws JMSException {
        return new MockQueueConnection();
    }

    public Connection createConnection(String s, String s1) throws JMSException {
        return new MockQueueConnection();
    }
}
