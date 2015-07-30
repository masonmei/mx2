// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.net;

import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import javax.jms.JMSException;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import javax.jms.ObjectMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.QueueSession;
import javax.jms.QueueConnection;
import javax.naming.Context;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import java.util.Hashtable;
import javax.naming.InitialContext;
import java.util.Properties;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.ContextInitializer;
import com.newrelic.agent.deps.org.slf4j.LoggerFactory;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import javax.jms.MessageListener;

public class JMSQueueSink implements MessageListener
{
    private Logger logger;
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            usage("Wrong number of arguments.");
        }
        final String qcfBindingName = args[0];
        final String queueBindingName = args[1];
        String username = null;
        String password = null;
        if (args.length == 4) {
            username = args[2];
            password = args[3];
        }
        final LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        new ContextInitializer(loggerContext).autoConfig();
        new JMSQueueSink(qcfBindingName, queueBindingName, username, password);
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Type \"exit\" to quit JMSQueueSink.");
        String s;
        do {
            s = stdin.readLine();
        } while (!s.equalsIgnoreCase("exit"));
        System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
    }
    
    public JMSQueueSink(final String qcfBindingName, final String queueBindingName, final String username, final String password) {
        this.logger = (Logger)LoggerFactory.getLogger(JMSTopicSink.class);
        try {
            final Properties env = new Properties();
            ((Hashtable<String, String>)env).put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            ((Hashtable<String, String>)env).put("java.naming.provider.url", "tcp://localhost:61616");
            final Context ctx = new InitialContext(env);
            final QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory)this.lookup(ctx, qcfBindingName);
            System.out.println("Queue Cnx Factory found");
            final Queue queue = (Queue)ctx.lookup(queueBindingName);
            System.out.println("Queue found: " + queue.getQueueName());
            final QueueConnection queueConnection = queueConnectionFactory.createQueueConnection(username, password);
            System.out.println("Queue Connection created");
            final QueueSession queueSession = queueConnection.createQueueSession(false, 1);
            final MessageConsumer queueConsumer = queueSession.createConsumer((Destination)queue);
            queueConsumer.setMessageListener((MessageListener)this);
            queueConnection.start();
            System.out.println("Queue Connection started");
        }
        catch (Exception e) {
            this.logger.error("Could not read JMS message.", e);
        }
    }
    
    public void onMessage(final Message message) {
        try {
            if (message instanceof ObjectMessage) {
                final ObjectMessage objectMessage = (ObjectMessage)message;
                final ILoggingEvent event = (ILoggingEvent)objectMessage.getObject();
                final Logger log = (Logger)LoggerFactory.getLogger(event.getLoggerName());
                log.callAppenders(event);
            }
            else {
                this.logger.warn("Received message is of type " + message.getJMSType() + ", was expecting ObjectMessage.");
            }
        }
        catch (JMSException jmse) {
            this.logger.error("Exception thrown while processing incoming message.", (Throwable)jmse);
        }
    }
    
    protected Object lookup(final Context ctx, final String name) throws NamingException {
        try {
            return ctx.lookup(name);
        }
        catch (NameNotFoundException e) {
            this.logger.error("Could not find name [" + name + "].");
            throw e;
        }
    }
    
    static void usage(final String msg) {
        System.err.println(msg);
        System.err.println("Usage: java " + JMSQueueSink.class.getName() + " QueueConnectionFactoryBindingName QueueBindingName Username Password");
        System.exit(1);
    }
}