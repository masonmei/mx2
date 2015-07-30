// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.newrelic.agent.deps.org.slf4j.helpers.MessageFormatter;
import java.util.Map;
import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import java.io.Serializable;

public class LoggingEventVO implements ILoggingEvent, Serializable
{
    private static final long serialVersionUID = 6553722650255690312L;
    private static final int NULL_ARGUMENT_ARRAY = -1;
    private static final String NULL_ARGUMENT_ARRAY_ELEMENT = "NULL_ARGUMENT_ARRAY_ELEMENT";
    private String threadName;
    private String loggerName;
    private LoggerContextVO loggerContextVO;
    private transient Level level;
    private String message;
    private transient String formattedMessage;
    private transient Object[] argumentArray;
    private ThrowableProxyVO throwableProxy;
    private StackTraceElement[] callerDataArray;
    private Marker marker;
    private Map<String, String> mdcPropertyMap;
    private long timeStamp;
    
    public static LoggingEventVO build(final ILoggingEvent le) {
        final LoggingEventVO ledo = new LoggingEventVO();
        ledo.loggerName = le.getLoggerName();
        ledo.loggerContextVO = le.getLoggerContextVO();
        ledo.threadName = le.getThreadName();
        ledo.level = le.getLevel();
        ledo.message = le.getMessage();
        ledo.argumentArray = le.getArgumentArray();
        ledo.marker = le.getMarker();
        ledo.mdcPropertyMap = le.getMDCPropertyMap();
        ledo.timeStamp = le.getTimeStamp();
        ledo.throwableProxy = ThrowableProxyVO.build(le.getThrowableProxy());
        if (le.hasCallerData()) {
            ledo.callerDataArray = le.getCallerData();
        }
        return ledo;
    }
    
    public String getThreadName() {
        return this.threadName;
    }
    
    public LoggerContextVO getLoggerContextVO() {
        return this.loggerContextVO;
    }
    
    public String getLoggerName() {
        return this.loggerName;
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getFormattedMessage() {
        if (this.formattedMessage != null) {
            return this.formattedMessage;
        }
        if (this.argumentArray != null) {
            this.formattedMessage = MessageFormatter.arrayFormat(this.message, this.argumentArray).getMessage();
        }
        else {
            this.formattedMessage = this.message;
        }
        return this.formattedMessage;
    }
    
    public Object[] getArgumentArray() {
        return this.argumentArray;
    }
    
    public IThrowableProxy getThrowableProxy() {
        return this.throwableProxy;
    }
    
    public StackTraceElement[] getCallerData() {
        return this.callerDataArray;
    }
    
    public boolean hasCallerData() {
        return this.callerDataArray != null;
    }
    
    public Marker getMarker() {
        return this.marker;
    }
    
    public long getTimeStamp() {
        return this.timeStamp;
    }
    
    public long getContextBirthTime() {
        return this.loggerContextVO.getBirthTime();
    }
    
    public LoggerContextVO getContextLoggerRemoteView() {
        return this.loggerContextVO;
    }
    
    public Map<String, String> getMDCPropertyMap() {
        return this.mdcPropertyMap;
    }
    
    public Map<String, String> getMdc() {
        return this.mdcPropertyMap;
    }
    
    public void prepareForDeferredProcessing() {
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.level.levelInt);
        if (this.argumentArray != null) {
            final int len = this.argumentArray.length;
            out.writeInt(len);
            for (int i = 0; i < this.argumentArray.length; ++i) {
                if (this.argumentArray[i] != null) {
                    out.writeObject(this.argumentArray[i].toString());
                }
                else {
                    out.writeObject("NULL_ARGUMENT_ARRAY_ELEMENT");
                }
            }
        }
        else {
            out.writeInt(-1);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int levelInt = in.readInt();
        this.level = Level.toLevel(levelInt);
        final int argArrayLen = in.readInt();
        if (argArrayLen != -1) {
            this.argumentArray = new String[argArrayLen];
            for (int i = 0; i < argArrayLen; ++i) {
                final Object val = in.readObject();
                if (!"NULL_ARGUMENT_ARRAY_ELEMENT".equals(val)) {
                    this.argumentArray[i] = val;
                }
            }
        }
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.message == null) ? 0 : this.message.hashCode());
        result = 31 * result + ((this.threadName == null) ? 0 : this.threadName.hashCode());
        result = 31 * result + (int)(this.timeStamp ^ this.timeStamp >>> 32);
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final LoggingEventVO other = (LoggingEventVO)obj;
        if (this.message == null) {
            if (other.message != null) {
                return false;
            }
        }
        else if (!this.message.equals(other.message)) {
            return false;
        }
        if (this.loggerName == null) {
            if (other.loggerName != null) {
                return false;
            }
        }
        else if (!this.loggerName.equals(other.loggerName)) {
            return false;
        }
        if (this.threadName == null) {
            if (other.threadName != null) {
                return false;
            }
        }
        else if (!this.threadName.equals(other.threadName)) {
            return false;
        }
        if (this.timeStamp != other.timeStamp) {
            return false;
        }
        if (this.marker == null) {
            if (other.marker != null) {
                return false;
            }
        }
        else if (!this.marker.equals(other.marker)) {
            return false;
        }
        if (this.mdcPropertyMap == null) {
            if (other.mdcPropertyMap != null) {
                return false;
            }
        }
        else if (!this.mdcPropertyMap.equals(other.mdcPropertyMap)) {
            return false;
        }
        return true;
    }
}
