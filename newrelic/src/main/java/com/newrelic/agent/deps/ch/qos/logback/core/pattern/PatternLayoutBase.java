// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser.Node;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser.Parser;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.LayoutBase;

public abstract class PatternLayoutBase<E> extends LayoutBase<E>
{
    Converter<E> head;
    String pattern;
    protected PostCompileProcessor<E> postCompileProcessor;
    Map<String, String> instanceConverterMap;
    protected boolean outputPatternAsHeader;
    
    public PatternLayoutBase() {
        this.instanceConverterMap = new HashMap<String, String>();
        this.outputPatternAsHeader = false;
    }
    
    public abstract Map<String, String> getDefaultConverterMap();
    
    public Map<String, String> getEffectiveConverterMap() {
        final Map<String, String> effectiveMap = new HashMap<String, String>();
        final Map<String, String> defaultMap = this.getDefaultConverterMap();
        if (defaultMap != null) {
            effectiveMap.putAll(defaultMap);
        }
        final Context context = this.getContext();
        if (context != null) {
            final Map<String, String> contextMap = (Map<String, String>)context.getObject("PATTERN_RULE_REGISTRY");
            if (contextMap != null) {
                effectiveMap.putAll(contextMap);
            }
        }
        effectiveMap.putAll(this.instanceConverterMap);
        return effectiveMap;
    }
    
    public void start() {
        if (this.pattern == null || this.pattern.length() == 0) {
            this.addError("Empty or null pattern.");
            return;
        }
        try {
            final Parser<E> p = new Parser<E>(this.pattern);
            if (this.getContext() != null) {
                p.setContext(this.getContext());
            }
            final Node t = p.parse();
            this.head = p.compile(t, this.getEffectiveConverterMap());
            if (this.postCompileProcessor != null) {
                this.postCompileProcessor.process(this.head);
            }
            ConverterUtil.setContextForConverters(this.getContext(), this.head);
            ConverterUtil.startConverters(this.head);
            super.start();
        }
        catch (ScanException sce) {
            final StatusManager sm = this.getContext().getStatusManager();
            sm.add(new ErrorStatus("Failed to parse pattern \"" + this.getPattern() + "\".", this, sce));
        }
    }
    
    public void setPostCompileProcessor(final PostCompileProcessor<E> postCompileProcessor) {
        this.postCompileProcessor = postCompileProcessor;
    }
    
    protected void setContextForConverters(final Converter<E> head) {
        ConverterUtil.setContextForConverters(this.getContext(), head);
    }
    
    protected String writeLoopOnConverters(final E event) {
        final StringBuilder buf = new StringBuilder(128);
        for (Converter<E> c = this.head; c != null; c = c.getNext()) {
            c.write(buf, event);
        }
        return buf.toString();
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    
    public String toString() {
        return this.getClass().getName() + "(\"" + this.getPattern() + "\")";
    }
    
    public Map<String, String> getInstanceConverterMap() {
        return this.instanceConverterMap;
    }
    
    protected String getPresentationHeaderPrefix() {
        return "";
    }
    
    public boolean isOutputPatternAsHeader() {
        return this.outputPatternAsHeader;
    }
    
    public void setOutputPatternAsHeader(final boolean outputPatternAsHeader) {
        this.outputPatternAsHeader = outputPatternAsHeader;
    }
    
    public String getPresentationHeader() {
        if (this.outputPatternAsHeader) {
            return this.getPresentationHeaderPrefix() + this.pattern;
        }
        return super.getPresentationHeader();
    }
}
