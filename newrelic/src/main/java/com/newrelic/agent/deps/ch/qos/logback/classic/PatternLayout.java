// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic;

import com.newrelic.agent.deps.ch.qos.logback.core.pattern.PostCompileProcessor;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BoldMagentaCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BoldGreenCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BoldRedCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.WhiteCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.CyanCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.MagentaCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BlueCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.YellowCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.GreenCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.RedCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.BlackCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.LineSeparatorConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.PropertyConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.MarkerConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.CallerDataConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ContextNameConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.ContextNameAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.NopThrowableInformationConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.MDCConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.FileOfCallerConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.LineOfCallerConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.MethodOfCallerConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.MessageConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.LoggerConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ThreadConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.LevelConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.RelativeTimeConverter;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.DateConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser.Parser;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.PatternLayoutBase;

public class PatternLayout extends PatternLayoutBase<ILoggingEvent>
{
    public static final Map<String, String> defaultConverterMap;
    public static final String HEADER_PREFIX = "#logback.classic pattern: ";
    
    public PatternLayout() {
        this.postCompileProcessor = new EnsureExceptionHandling();
    }
    
    public Map<String, String> getDefaultConverterMap() {
        return PatternLayout.defaultConverterMap;
    }
    
    public String doLayout(final ILoggingEvent event) {
        if (!this.isStarted()) {
            return "";
        }
        return this.writeLoopOnConverters(event);
    }
    
    protected String getPresentationHeaderPrefix() {
        return "#logback.classic pattern: ";
    }
    
    static {
        (defaultConverterMap = new HashMap<String, String>()).putAll(Parser.DEFAULT_COMPOSITE_CONVERTER_MAP);
        PatternLayout.defaultConverterMap.put("d", DateConverter.class.getName());
        PatternLayout.defaultConverterMap.put("date", DateConverter.class.getName());
        PatternLayout.defaultConverterMap.put("r", RelativeTimeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("relative", RelativeTimeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("level", LevelConverter.class.getName());
        PatternLayout.defaultConverterMap.put("le", LevelConverter.class.getName());
        PatternLayout.defaultConverterMap.put("p", LevelConverter.class.getName());
        PatternLayout.defaultConverterMap.put("t", ThreadConverter.class.getName());
        PatternLayout.defaultConverterMap.put("thread", ThreadConverter.class.getName());
        PatternLayout.defaultConverterMap.put("lo", LoggerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("logger", LoggerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("c", LoggerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("m", MessageConverter.class.getName());
        PatternLayout.defaultConverterMap.put("msg", MessageConverter.class.getName());
        PatternLayout.defaultConverterMap.put("message", MessageConverter.class.getName());
        PatternLayout.defaultConverterMap.put("C", ClassOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("class", ClassOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("M", MethodOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("method", MethodOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("L", LineOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("line", LineOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("F", FileOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("file", FileOfCallerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("X", MDCConverter.class.getName());
        PatternLayout.defaultConverterMap.put("mdc", MDCConverter.class.getName());
        PatternLayout.defaultConverterMap.put("ex", ThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("exception", ThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("rEx", RootCauseFirstThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("rootException", RootCauseFirstThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("throwable", ThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("xEx", ExtendedThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("xException", ExtendedThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("xThrowable", ExtendedThrowableProxyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("nopex", NopThrowableInformationConverter.class.getName());
        PatternLayout.defaultConverterMap.put("nopexception", NopThrowableInformationConverter.class.getName());
        PatternLayout.defaultConverterMap.put("cn", ContextNameAction.class.getName());
        PatternLayout.defaultConverterMap.put("contextName", ContextNameConverter.class.getName());
        PatternLayout.defaultConverterMap.put("caller", CallerDataConverter.class.getName());
        PatternLayout.defaultConverterMap.put("marker", MarkerConverter.class.getName());
        PatternLayout.defaultConverterMap.put("property", PropertyConverter.class.getName());
        PatternLayout.defaultConverterMap.put("n", LineSeparatorConverter.class.getName());
        PatternLayout.defaultConverterMap.put("black", BlackCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("red", RedCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("green", GreenCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("yellow", YellowCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("blue", BlueCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("magenta", MagentaCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("cyan", CyanCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("white", WhiteCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("boldRed", BoldRedCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("boldGreen", BoldGreenCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("boldYellow", BoldYellowCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("boldBlue", BoldBlueCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("boldMagenta", BoldMagentaCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("boldCyan", BoldCyanCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("boldWhite", BoldWhiteCompositeConverter.class.getName());
        PatternLayout.defaultConverterMap.put("highlight", HighlightingCompositeConverter.class.getName());
    }
}
