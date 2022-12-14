package com.cloudweb.oa.mq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestActiveMQConsumer extends Thread implements MessageListener {


    private Session session;
    private Destination destination;
    private MessageProducer replyProducer;

    /**
     * @return org.apache.activemq.ActiveMQConnectionFactory
     * @default
     */
    public ActiveMQConnectionFactory getConnectionFactory() {
        // default null
        String user = ActiveMQConnection.DEFAULT_USER;
        // default null
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        // default failover://tcp://localhost:61616
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;

        user = "redmoon";
        password = "redmoon";
        url = "tcp://localhost:61616";
        return this.getConnectionFactory(user, password, url);
    }

    /**
     * @param user     java.lang.String
     * @param password java.lang.String
     * @param url      java.lang.String
     * @return org.apache.activemq.ActiveMQConnectionFactory
     */
    public ActiveMQConnectionFactory getConnectionFactory(String user,
                                                          String password, String url) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                user, password, url);
        return connectionFactory;
    }


    /**
     * @return javax.jms.Connection
     */
    public Connection getConnection() {
        ActiveMQConnectionFactory connectionFactory = this.getConnectionFactory();
        return this.getConnection(connectionFactory);
    }

    /**
     * @param connectionFactory org.apache.activemq.ActiveMQConnectionFactory
     * @return javax.jms.Connection
     */
    public Connection getConnection(ActiveMQConnectionFactory connectionFactory) {
        Connection connection = null;
        try {
            if (connectionFactory != null) {
                connection = connectionFactory.createConnection();
                connection.start();
            }
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
        return connection;
    }

    /**
     * @return javax.jms.Session ??????
     */
    public Session getSession() {
        Connection connection = this.getConnection();
        boolean transacted = false;
        int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
        return this.getSession(connection, transacted, acknowledgeMode);
    }

    /**
     * @param connection      javax.jms.Connection
     * @param transacted      boolean ?????????????????????
     * @param acknowledgeMode int acknowledge ??????
     * @return javax.jms.Session ??????
     */
    public Session getSession(Connection connection, boolean transacted,
                              int acknowledgeMode) {
        if (connection != null) {
            if (session == null) {
                try {
                    session = connection.createSession(transacted, acknowledgeMode);
                } catch (JMSException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return session;
    }

    /**
     * @param subject java.lang.String ????????????
     * @return javax.jms.Destination
     */
    public Destination createDestination(String subject) {
        String mode = "Point-to-Point";
        return this.createDestination(mode, subject);
    }

    /**
     * @param mode    java.lang.String ??????????????????
     * @param subject java.lang.String ????????????
     * @return javax.jms.Destination
     */
    public Destination createDestination(String mode, String subject) {
        session = this.getSession();
        return this.createDestination(mode, session, subject);
    }

    /**
     * @param mode    java.lang.String ??????????????????
     * @param session javax.jms.Session ??????
     * @param subject java.lang.String ????????????
     * @return javax.jms.Destination
     */
    public Destination createDestination(String mode, Session session,
                                         String subject) {

        if (session != null && mode != null && !mode.trim().equals("")) {
            try {
                if (mode.trim().equals("Publisher/Subscriber")) {
                    destination = session.createTopic(subject);
                } else if (mode.trim().equals("Point-to-Point")) {
                    destination = session.createQueue(subject);
                }
            } catch (JMSException e) {
                e.printStackTrace();
                return null;
            }
        }
        return destination;
    }

    /**
     * @return
     */
    public MessageProducer createReplyer() {
        Session session = this.getSession();
        try {
            replyProducer = session.createProducer(null);
            replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return replyProducer;
    }

    /**
     * @return javax.jms.MsgConsumer
     */
    public MessageConsumer createConsumer() {
        Session session = this.getSession();
        MessageConsumer consumer = null;
        Destination destination = this.createDestination("moduleLog");//session.createQueue(subject);
        try {
            consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return consumer;
    }

    @Override
    public void onMessage(Message message) {
        try {

            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;

                String msg = txtMsg.getText();
                int length = msg.length();
                System.out.println("[" + this.getName() + "] Received: '" + msg + "' (length " + length + ")");
            }

            if (message.getJMSReplyTo() != null) {
                Session session = this.getSession();
                MessageProducer replyProducer = this.createReplyer();
                replyProducer.send(message.getJMSReplyTo(), session.createTextMessage("Reply: " + message.getJMSMessageID()));
            }
            message.acknowledge();
        } catch (JMSException e) {
            System.out.println("[" + this.getName() + "] Caught: " + e);
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void run() {
        //????????????
        session = this.getSession();
        //??????Destination
        destination = this.createDestination("moduleLog");
        //??????replyProducer
        replyProducer = this.createReplyer();
        MessageConsumer consumer = this.createConsumer();
        try {
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReceiveMessage() {
        ArrayList<TestActiveMQConsumer> threads = new ArrayList<TestActiveMQConsumer>();
        for (int threadCount = 1; threadCount <= 1; threadCount++) {
            TestActiveMQConsumer consumer = new TestActiveMQConsumer();
            consumer.start();
            threads.add(consumer);
        }
        while (true) {
            Iterator<TestActiveMQConsumer> itr = threads.iterator();
            int running = 0;
            while (itr.hasNext()) {
                TestActiveMQConsumer thread = itr.next();
                if (thread.isAlive()) {
                    running++;
                }
            }
            if (running <= 0) {
                System.out.println("All threads completed their work");
                break;
            }
        }
    }

    @Test
    public void get() {
        try {
            String url = "tcp://localhost:61616";
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            // ??????????????????????????????????????????????????????conf????????????credentials.properties????????????????????????activemq.xml?????????
            connectionFactory.setUserName("redmoon");
            connectionFactory.setPassword("redmoon");
            // ????????????
            Connection connection = connectionFactory.createConnection();
            connection.start();
            // ??????Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // ???????????????????????????????????????????????????
            Destination destination = session.createQueue("moduleLog");
            // ?????????????????????
            MessageConsumer consumer = session.createConsumer(destination);
            // ?????????????????????????????????????????????????????????0?????????????????????receive????????????????????????????????????????????????????????????????????????null
            Message message = consumer.receive(1000);
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Received: " + text);
            } else {
                System.out.println("Received: " + message);
            }
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}