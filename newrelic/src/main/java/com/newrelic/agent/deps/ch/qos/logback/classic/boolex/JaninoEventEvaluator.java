// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.boolex;

import java.util.Map;
import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggerContextVO;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.IThrowableProxy;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ThrowableProxy;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.Matcher;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.JaninoEventEvaluatorBase;

public class JaninoEventEvaluator extends JaninoEventEvaluatorBase<ILoggingEvent>
{
    public static final String IMPORT_LEVEL = "import ch.qos.logback.classic.Level;\r\n";
    public static final List<String> DEFAULT_PARAM_NAME_LIST;
    public static final List<Class> DEFAULT_PARAM_TYPE_LIST;
    
    protected String getDecoratedExpression() {
        String expression = this.getExpression();
        if (!expression.contains("return")) {
            expression = "return " + expression + ";";
            this.addInfo("Adding [return] prefix and a semicolon suffix. Expression becomes [" + expression + "]");
            this.addInfo("See also http://logback.qos.ch/codes.html#block");
        }
        return "import ch.qos.logback.classic.Level;\r\n" + expression;
    }
    
    protected String[] getParameterNames() {
        final List<String> fullNameList = new ArrayList<String>();
        fullNameList.addAll(JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST);
        for (int i = 0; i < this.matcherList.size(); ++i) {
            final Matcher m = this.matcherList.get(i);
            fullNameList.add(m.getName());
        }
        return fullNameList.toArray(CoreConstants.EMPTY_STRING_ARRAY);
    }
    
    protected Class[] getParameterTypes() {
        final List<Class> fullTypeList = new ArrayList<Class>();
        fullTypeList.addAll(JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST);
        for (int i = 0; i < this.matcherList.size(); ++i) {
            fullTypeList.add(Matcher.class);
        }
        return fullTypeList.toArray(CoreConstants.EMPTY_CLASS_ARRAY);
    }
    
    protected Object[] getParameterValues(final ILoggingEvent loggingEvent) {
        final int matcherListSize = this.matcherList.size();
        int i = 0;
        final Object[] values = new Object[JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.size() + matcherListSize];
        values[i++] = Level.DEBUG_INTEGER;
        values[i++] = Level.INFO_INTEGER;
        values[i++] = Level.WARN_INTEGER;
        values[i++] = Level.ERROR_INTEGER;
        values[i++] = loggingEvent;
        values[i++] = loggingEvent.getMessage();
        values[i++] = loggingEvent.getFormattedMessage();
        values[i++] = loggingEvent.getLoggerName();
        values[i++] = loggingEvent.getLoggerContextVO();
        values[i++] = loggingEvent.getLevel().toInteger();
        values[i++] = loggingEvent.getTimeStamp();
        values[i++] = loggingEvent.getMarker();
        values[i++] = loggingEvent.getMDCPropertyMap();
        final IThrowableProxy iThrowableProxy = loggingEvent.getThrowableProxy();
        if (iThrowableProxy != null) {
            values[i++] = iThrowableProxy;
            if (iThrowableProxy instanceof ThrowableProxy) {
                values[i++] = ((ThrowableProxy)iThrowableProxy).getThrowable();
            }
            else {
                values[i++] = null;
            }
        }
        else {
            values[i++] = null;
            values[i++] = null;
        }
        for (int j = 0; j < matcherListSize; ++j) {
            values[i++] = this.matcherList.get(j);
        }
        return values;
    }
    
    static {
        DEFAULT_PARAM_NAME_LIST = new ArrayList<String>();
        DEFAULT_PARAM_TYPE_LIST = new ArrayList<Class>();
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("DEBUG");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("INFO");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("WARN");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("ERROR");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("event");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("message");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("formattedMessage");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("logger");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("loggerContext");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("level");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("timeStamp");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("marker");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("mdc");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("throwableProxy");
        JaninoEventEvaluator.DEFAULT_PARAM_NAME_LIST.add("throwable");
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(ILoggingEvent.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(String.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(String.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(String.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(LoggerContextVO.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Integer.TYPE);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Long.TYPE);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Marker.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Map.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(IThrowableProxy.class);
        JaninoEventEvaluator.DEFAULT_PARAM_TYPE_LIST.add(Throwable.class);
    }
}
