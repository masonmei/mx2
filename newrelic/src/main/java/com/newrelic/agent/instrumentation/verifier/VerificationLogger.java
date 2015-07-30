// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.verifier;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import com.newrelic.agent.logging.IAgentLogger;

public class VerificationLogger implements IAgentLogger
{
    private List<String> loggingOutput;
    
    public VerificationLogger() {
        this.loggingOutput = new LinkedList<String>();
    }
    
    private String stackTraceToString(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
    
    public List<String> getOutput() {
        return this.loggingOutput;
    }
    
    public void flush() {
        this.loggingOutput = new LinkedList<String>();
    }
    
    public boolean isLoggable(final Level level) {
        return true;
    }
    
    public void log(final Level level, final String pattern, final Object... parts) {
        this.loggingOutput.add(MessageFormat.format(pattern, parts));
    }
    
    public void log(final Level level, final Throwable t, final String pattern, final Object... msg) {
        this.loggingOutput.add(MessageFormat.format(pattern, msg));
        this.loggingOutput.add(this.stackTraceToString(t));
    }
    
    public void logToChild(final String childName, final Level level, final String pattern, final Object... parts) {
    }
    
    public void severe(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void error(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void warning(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void info(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void config(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void fine(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void finer(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void finest(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void debug(final String message) {
        this.loggingOutput.add(message);
    }
    
    public void trace(final String message) {
        this.loggingOutput.add(message);
    }
    
    public boolean isFineEnabled() {
        return true;
    }
    
    public boolean isFinerEnabled() {
        return true;
    }
    
    public boolean isFinestEnabled() {
        return true;
    }
    
    public boolean isDebugEnabled() {
        return false;
    }
    
    public boolean isTraceEnabled() {
        return false;
    }
    
    public void log(final Level level, final String message, final Throwable throwable) {
    }
    
    public void log(final Level level, final String message) {
        this.loggingOutput.add(message);
    }
    
    public void log(final Level level, final String message, final Object[] args, final Throwable throwable) {
    }
    
    public IAgentLogger getChildLogger(final Class<?> clazz) {
        return null;
    }
    
    public IAgentLogger getChildLogger(final String fullName) {
        return null;
    }
}
