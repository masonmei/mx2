// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.profile.method.MethodInfoUtil;
import java.util.Map;
import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.Arrays;
import java.io.Serializable;
import java.io.Writer;
import com.newrelic.agent.profile.method.MethodInfo;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class ProfiledMethod implements JSONStreamAware
{
    private final StackTraceElement stackTraceElement;
    private final int hashCode;
    private MethodInfo info;
    
    private ProfiledMethod(final StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
        this.hashCode = stackTraceElement.hashCode();
    }
    
    public static ProfiledMethod newProfiledMethod(final StackTraceElement stackElement) {
        if (stackElement == null) {
            return null;
        }
        if (stackElement.getClassName() == null) {
            return null;
        }
        if (stackElement.getMethodName() == null) {
            return null;
        }
        return new ProfiledMethod(stackElement);
    }
    
    public String getFullMethodName() {
        return this.getClassName() + ":" + this.getMethodName();
    }
    
    public String getMethodName() {
        return this.stackTraceElement.getMethodName();
    }
    
    public String getClassName() {
        return this.stackTraceElement.getClassName();
    }
    
    public final int getLineNumber() {
        return this.stackTraceElement.getLineNumber();
    }
    
    public int hashCode() {
        return this.hashCode;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ProfiledMethod other = (ProfiledMethod)obj;
        return other.stackTraceElement.equals(this.stackTraceElement);
    }
    
    public String toString() {
        return this.getFullMethodName() + ":" + this.getLineNumber();
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        if (this.info == null) {
            JSONArray.writeJSONString(Arrays.asList(this.getClassName(), this.getMethodName(), this.getLineNumber()), out);
        }
        else {
            JSONArray.writeJSONString(Arrays.asList(this.getClassName(), this.getMethodName(), this.getLineNumber(), this.info.getJsonMethodMaps()), out);
        }
    }
    
    void setMethodDetails(final Map<String, Class<?>> classMap) {
        final Class<?> declaringClass = classMap.get(this.getClassName());
        if (declaringClass != null) {
            try {
                this.info = MethodInfoUtil.createMethodInfo(declaringClass, this.getMethodName(), this.getLineNumber());
            }
            catch (Throwable e) {
                Agent.LOG.log(Level.FINER, e, "Error finding MethodInfo for {0}.{1}", new Object[] { declaringClass.getName(), this.getMethodName() });
                this.info = null;
            }
        }
    }
}
