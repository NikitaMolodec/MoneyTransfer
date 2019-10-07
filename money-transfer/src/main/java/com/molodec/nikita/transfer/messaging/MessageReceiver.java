package com.molodec.nikita.transfer.messaging;

import io.dropwizard.lifecycle.Managed;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

public class MessageReceiver<T extends Serializable> implements Managed {
    private final ConnectionFactory connectionFactory;
    private final String queueName;

    private Consumer<T> consumer;
    private Connection connection = null;
    private Session session = null;

    public MessageReceiver(ConnectionFactory connectionFactory, String queueName, Consumer<T> consumer) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
        this.queueName = Objects.requireNonNull(queueName);
        this.consumer = Objects.requireNonNull(consumer);
    }


    @Override
    public void start() throws Exception {
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer messageConsumer = session.createConsumer(session.createQueue(queueName));
        messageConsumer.setMessageListener(new MessageListenerContainer<T>(consumer));
    }

    @Override
    public void stop() throws Exception {
        session.close();
        connection.close();
    }
}
