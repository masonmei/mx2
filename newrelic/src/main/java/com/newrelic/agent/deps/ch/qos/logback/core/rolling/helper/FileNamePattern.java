// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.LiteralConverter;
import java.util.Date;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser.Node;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.IEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser.Parser;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.ConverterUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.Converter;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class FileNamePattern extends ContextAwareBase
{
    static final Map<String, String> CONVERTER_MAP;
    String pattern;
    Converter<Object> headTokenConverter;
    
    public FileNamePattern(final String patternArg, final Context contextArg) {
        this.setPattern(FileFilterUtil.slashify(patternArg));
        this.setContext(contextArg);
        this.parse();
        ConverterUtil.startConverters(this.headTokenConverter);
    }
    
    void parse() {
        try {
            final String patternForParsing = this.escapeRightParantesis(this.pattern);
            final Parser<Object> p = new Parser<Object>(patternForParsing, new AlmostAsIsEscapeUtil());
            p.setContext(this.context);
            final Node t = p.parse();
            this.headTokenConverter = p.compile(t, FileNamePattern.CONVERTER_MAP);
        }
        catch (ScanException sce) {
            this.addError("Failed to parse pattern \"" + this.pattern + "\".", sce);
        }
    }
    
    String escapeRightParantesis(final String in) {
        return this.pattern.replace(")", "\\)");
    }
    
    public String toString() {
        return this.pattern;
    }
    
    public DateTokenConverter getPrimaryDateTokenConverter() {
        for (Converter p = this.headTokenConverter; p != null; p = p.getNext()) {
            if (p instanceof DateTokenConverter) {
                final DateTokenConverter dtc = (DateTokenConverter)p;
                if (dtc.isPrimary()) {
                    return dtc;
                }
            }
        }
        return null;
    }
    
    public IntegerTokenConverter getIntegerTokenConverter() {
        for (Converter p = this.headTokenConverter; p != null; p = p.getNext()) {
            if (p instanceof IntegerTokenConverter) {
                return (IntegerTokenConverter)p;
            }
        }
        return null;
    }
    
    public String convertMultipleArguments(final Object... objectList) {
        final StringBuilder buf = new StringBuilder();
        for (Converter<Object> c = this.headTokenConverter; c != null; c = c.getNext()) {
            if (c instanceof MonoTypedConverter) {
                final MonoTypedConverter monoTyped = (MonoTypedConverter)c;
                for (final Object o : objectList) {
                    if (monoTyped.isApplicable(o)) {
                        buf.append(c.convert(o));
                    }
                }
            }
            else {
                buf.append(c.convert(objectList));
            }
        }
        return buf.toString();
    }
    
    public String convert(final Object o) {
        final StringBuilder buf = new StringBuilder();
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            buf.append(p.convert(o));
        }
        return buf.toString();
    }
    
    public String convertInt(final int i) {
        return this.convert(i);
    }
    
    public void setPattern(final String pattern) {
        if (pattern != null) {
            this.pattern = pattern.trim();
        }
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public String toRegex(final Date date) {
        final StringBuilder buf = new StringBuilder();
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            if (p instanceof LiteralConverter) {
                buf.append(p.convert(null));
            }
            else if (p instanceof IntegerTokenConverter) {
                buf.append("(\\d{1,3})");
            }
            else if (p instanceof DateTokenConverter) {
                buf.append(p.convert(date));
            }
        }
        return buf.toString();
    }
    
    public String toRegex() {
        final StringBuilder buf = new StringBuilder();
        for (Converter<Object> p = this.headTokenConverter; p != null; p = p.getNext()) {
            if (p instanceof LiteralConverter) {
                buf.append(p.convert(null));
            }
            else if (p instanceof IntegerTokenConverter) {
                buf.append("\\d{1,2}");
            }
            else if (p instanceof DateTokenConverter) {
                final DateTokenConverter<Object> dtc = (DateTokenConverter<Object>)(DateTokenConverter)p;
                buf.append(dtc.toRegex());
            }
        }
        return buf.toString();
    }
    
    static {
        (CONVERTER_MAP = new HashMap<String, String>()).put("i", IntegerTokenConverter.class.getName());
        FileNamePattern.CONVERTER_MAP.put("d", DateTokenConverter.class.getName());
    }
}
