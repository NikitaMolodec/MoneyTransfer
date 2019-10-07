package com.molodec.nikita.transfer.messaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Objects;

public class MessageSender<T extends Serializable> {
    private final ConnectionFactory connectionFactory;
    private final String queueName;

    public MessageSender(ConnectionFactory connectionFactory, String queueName) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
        this.queueName = Objects.requireNonNull(queueName);
    }

    public void send(T toSend) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue(queueName));
            producer.send(session.createObjectMessage(toSend));
        } finally {
            if (Objects.nonNull(session)) session.close();
            if (Objects.nonNull(connection)) connection.close();
        }

    }
}
