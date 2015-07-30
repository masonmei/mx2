// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.CallerData;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EvaluationException;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluator;
import java.util.List;

public class CallerDataConverter extends ClassicConverter
{
    public static final String DEFAULT_CALLER_LINE_PREFIX = "Caller+";
    int depth;
    List<EventEvaluator<ILoggingEvent>> evaluatorList;
    final int MAX_ERROR_COUNT = 4;
    int errorCount;
    
    public CallerDataConverter() {
        this.depth = 5;
        this.evaluatorList = null;
        this.errorCount = 0;
    }
    
    public void start() {
        final String depthStr = this.getFirstOption();
        if (depthStr == null) {
            return;
        }
        try {
            this.depth = Integer.parseInt(depthStr);
        }
        catch (NumberFormatException nfe) {
            this.addError("Failed to parse depth option [" + depthStr + "]", nfe);
        }
        final List optionList = this.getOptionList();
        if (optionList != null && optionList.size() > 1) {
            for (int optionListSize = optionList.size(), i = 1; i < optionListSize; ++i) {
                final String evaluatorStr = optionList.get(i);
                final Context context = this.getContext();
                if (context != null) {
                    final Map evaluatorMap = (Map)context.getObject("EVALUATOR_MAP");
                    final EventEvaluator<ILoggingEvent> ee = evaluatorMap.get(evaluatorStr);
                    if (ee != null) {
                        this.addEvaluator(ee);
                    }
                }
            }
        }
    }
    
    private void addEvaluator(final EventEvaluator<ILoggingEvent> ee) {
        if (this.evaluatorList == null) {
            this.evaluatorList = new ArrayList<EventEvaluator<ILoggingEvent>>();
        }
        this.evaluatorList.add(ee);
    }
    
    public String convert(final ILoggingEvent le) {
        final StringBuilder buf = new StringBuilder();
        if (this.evaluatorList != null) {
            boolean printCallerData = false;
            for (int i = 0; i < this.evaluatorList.size(); ++i) {
                final EventEvaluator<ILoggingEvent> ee = this.evaluatorList.get(i);
                try {
                    if (ee.evaluate(le)) {
                        printCallerData = true;
                        break;
                    }
                }
                catch (EvaluationException eex) {
                    ++this.errorCount;
                    if (this.errorCount < 4) {
                        this.addError("Exception thrown for evaluator named [" + ee.getName() + "]", eex);
                    }
                    else if (this.errorCount == 4) {
                        final ErrorStatus errorStatus = new ErrorStatus("Exception thrown for evaluator named [" + ee.getName() + "].", this, eex);
                        errorStatus.add(new ErrorStatus("This was the last warning about this evaluator's errors.We don't want the StatusManager to get flooded.", this));
                        this.addStatus(errorStatus);
                    }
                }
            }
            if (!printCallerData) {
                return "";
            }
        }
        final StackTraceElement[] cda = le.getCallerData();
        if (cda != null && cda.length > 0) {
            for (int limit = (this.depth < cda.length) ? this.depth : cda.length, j = 0; j < limit; ++j) {
                buf.append(this.getCallerLinePrefix());
                buf.append(j);
                buf.append("\t at ");
                buf.append(cda[j]);
                buf.append(CoreConstants.LINE_SEPARATOR);
            }
            return buf.toString();
        }
        return CallerData.CALLER_DATA_NA;
    }
    
    protected String getCallerLinePrefix() {
        return "Caller+";
    }
}
