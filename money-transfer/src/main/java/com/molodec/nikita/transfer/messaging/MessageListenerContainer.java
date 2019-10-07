package com.molodec.nikita.transfer.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.function.Consumer;

public class MessageListenerContainer<T extends Serializable> implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListenerContainer.class);

    private final Consumer<T> consumer;

    public MessageListenerContainer(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            @SuppressWarnings("uncheked")
            T o = (T) objectMessage.getObject();
            consumer.accept(o);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
