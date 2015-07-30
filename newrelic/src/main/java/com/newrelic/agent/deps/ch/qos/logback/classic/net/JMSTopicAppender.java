// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.net;

import java.io.Serializable;
import javax.jms.ObjectMessage;
import javax.jms.Message;
import javax.naming.Context;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PreSerializationTransformer;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicConnection;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.net.JMSAppenderBase;

public class JMSTopicAppender extends JMSAppenderBase<ILoggingEvent>
{
    static int SUCCESSIVE_FAILURE_LIMIT;
    String topicBindingName;
    String tcfBindingName;
    TopicConnection topicConnection;
    TopicSession topicSession;
    TopicPublisher topicPublisher;
    int successiveFailureCount;
    private PreSerializationTransformer<ILoggingEvent> pst;
    
    public JMSTopicAppender() {
        this.successiveFailureCount = 0;
        this.pst = new LoggingEventPreSerializationTransformer();
    }
    
    public void setTopicConnectionFactoryBindingName(final String tcfBindingName) {
        this.tcfBindingName = tcfBindingName;
    }
    
    public String getTopicConnectionFactoryBindingName() {
        return this.tcfBindingName;
    }
    
    public void setTopicBindingName(final String topicBindingName) {
        this.topicBindingName = topicBindingName;
    }
    
    public String getTopicBindingName() {
        return this.topicBindingName;
    }
    
    public void start() {
        try {
            final Context jndi = this.buildJNDIContext();
            final TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)this.lookup(jndi, this.tcfBindingName);
            if (this.userName != null) {
                this.topicConnection = topicConnectionFactory.createTopicConnection(this.userName, this.password);
            }
            else {
                this.topicConnection = topicConnectionFactory.createTopicConnection();
            }
            this.topicSession = this.topicConnection.createTopicSession(false, 1);
            final Topic topic = (Topic)this.lookup(jndi, this.topicBindingName);
            this.topicPublisher = this.topicSession.createPublisher(topic);
            this.topicConnection.start();
            jndi.close();
        }
        catch (Exception e) {
            this.addError("Error while activating options for appender named [" + this.name + "].", e);
        }
        if (this.topicConnection != null && this.topicSession != null && this.topicPublisher != null) {
            super.start();
        }
    }
    
    public synchronized void stop() {
        if (!this.started) {
            return;
        }
        this.started = false;
        try {
            if (this.topicSession != null) {
                this.topicSession.close();
            }
            if (this.topicConnection != null) {
                this.topicConnection.close();
            }
        }
        catch (Exception e) {
            this.addError("Error while closing JMSAppender [" + this.name + "].", e);
        }
        this.topicPublisher = null;
        this.topicSession = null;
        this.topicConnection = null;
    }
    
    public void append(final ILoggingEvent event) {
        if (!this.isStarted()) {
            return;
        }
        try {
            final ObjectMessage msg = this.topicSession.createObjectMessage();
            final Serializable so = this.pst.transform(event);
            msg.setObject(so);
            this.topicPublisher.publish((Message)msg);
            this.successiveFailureCount = 0;
        }
        catch (Exception e) {
            ++this.successiveFailureCount;
            if (this.successiveFailureCount > JMSTopicAppender.SUCCESSIVE_FAILURE_LIMIT) {
                this.stop();
            }
            this.addError("Could not publish message in JMSTopicAppender [" + this.name + "].", e);
        }
    }
    
    protected TopicConnection getTopicConnection() {
        return this.topicConnection;
    }
    
    protected TopicSession getTopicSession() {
        return this.topicSession;
    }
    
    protected TopicPublisher getTopicPublisher() {
        return this.topicPublisher;
    }
    
    static {
        JMSTopicAppender.SUCCESSIVE_FAILURE_LIMIT = 3;
    }
}
