// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.net;

import java.io.Serializable;
import javax.jms.ObjectMessage;
import javax.jms.Message;
import javax.naming.Context;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PreSerializationTransformer;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.QueueConnection;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.net.JMSAppenderBase;

public class JMSQueueAppender extends JMSAppenderBase<ILoggingEvent>
{
    static int SUCCESSIVE_FAILURE_LIMIT;
    String queueBindingName;
    String qcfBindingName;
    QueueConnection queueConnection;
    QueueSession queueSession;
    QueueSender queueSender;
    int successiveFailureCount;
    private PreSerializationTransformer<ILoggingEvent> pst;
    
    public JMSQueueAppender() {
        this.successiveFailureCount = 0;
        this.pst = new LoggingEventPreSerializationTransformer();
    }
    
    public void setQueueConnectionFactoryBindingName(final String qcfBindingName) {
        this.qcfBindingName = qcfBindingName;
    }
    
    public String getQueueConnectionFactoryBindingName() {
        return this.qcfBindingName;
    }
    
    public void setQueueBindingName(final String queueBindingName) {
        this.queueBindingName = queueBindingName;
    }
    
    public String getQueueBindingName() {
        return this.queueBindingName;
    }
    
    public void start() {
        try {
            final Context jndi = this.buildJNDIContext();
            final QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory)this.lookup(jndi, this.qcfBindingName);
            if (this.userName != null) {
                this.queueConnection = queueConnectionFactory.createQueueConnection(this.userName, this.password);
            }
            else {
                this.queueConnection = queueConnectionFactory.createQueueConnection();
            }
            this.queueSession = this.queueConnection.createQueueSession(false, 1);
            final Queue queue = (Queue)this.lookup(jndi, this.queueBindingName);
            this.queueSender = this.queueSession.createSender(queue);
            this.queueConnection.start();
            jndi.close();
        }
        catch (Exception e) {
            this.addError("Error while activating options for appender named [" + this.name + "].", e);
        }
        if (this.queueConnection != null && this.queueSession != null && this.queueSender != null) {
            super.start();
        }
    }
    
    public synchronized void stop() {
        if (!this.started) {
            return;
        }
        this.started = false;
        try {
            if (this.queueSession != null) {
                this.queueSession.close();
            }
            if (this.queueConnection != null) {
                this.queueConnection.close();
            }
        }
        catch (Exception e) {
            this.addError("Error while closing JMSAppender [" + this.name + "].", e);
        }
        this.queueSender = null;
        this.queueSession = null;
        this.queueConnection = null;
    }
    
    public void append(final ILoggingEvent event) {
        if (!this.isStarted()) {
            return;
        }
        try {
            final ObjectMessage msg = this.queueSession.createObjectMessage();
            final Serializable so = this.pst.transform(event);
            msg.setObject(so);
            this.queueSender.send((Message)msg);
            this.successiveFailureCount = 0;
        }
        catch (Exception e) {
            ++this.successiveFailureCount;
            if (this.successiveFailureCount > JMSQueueAppender.SUCCESSIVE_FAILURE_LIMIT) {
                this.stop();
            }
            this.addError("Could not send message in JMSQueueAppender [" + this.name + "].", e);
        }
    }
    
    protected QueueConnection getQueueConnection() {
        return this.queueConnection;
    }
    
    protected QueueSession getQueueSession() {
        return this.queueSession;
    }
    
    protected QueueSender getQueueSender() {
        return this.queueSender;
    }
    
    static {
        JMSQueueAppender.SUCCESSIVE_FAILURE_LIMIT = 3;
    }
}
