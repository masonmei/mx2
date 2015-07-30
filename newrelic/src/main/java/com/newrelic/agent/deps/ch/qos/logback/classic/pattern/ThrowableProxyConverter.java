// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ThrowableProxyUtil;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.IThrowableProxy;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EvaluationException;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.StackTraceElementProxy;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluator;
import java.util.List;

public class ThrowableProxyConverter extends ThrowableHandlingConverter
{
    int lengthOption;
    List<EventEvaluator<ILoggingEvent>> evaluatorList;
    int errorCount;
    
    public ThrowableProxyConverter() {
        this.evaluatorList = null;
        this.errorCount = 0;
    }
    
    public void start() {
        String lengthStr = this.getFirstOption();
        if (lengthStr == null) {
            this.lengthOption = Integer.MAX_VALUE;
        }
        else {
            lengthStr = lengthStr.toLowerCase();
            if ("full".equals(lengthStr)) {
                this.lengthOption = Integer.MAX_VALUE;
            }
            else if ("short".equals(lengthStr)) {
                this.lengthOption = 2;
            }
            else {
                try {
                    this.lengthOption = Integer.parseInt(lengthStr) + 1;
                }
                catch (NumberFormatException nfe) {
                    this.addError("Could not parser [" + lengthStr + " as an integer");
                    this.lengthOption = Integer.MAX_VALUE;
                }
            }
        }
        final List optionList = this.getOptionList();
        if (optionList != null && optionList.size() > 1) {
            for (int optionListSize = optionList.size(), i = 1; i < optionListSize; ++i) {
                final String evaluatorStr = optionList.get(i);
                final Context context = this.getContext();
                final Map evaluatorMap = (Map)context.getObject("EVALUATOR_MAP");
                final EventEvaluator<ILoggingEvent> ee = evaluatorMap.get(evaluatorStr);
                this.addEvaluator(ee);
            }
        }
        super.start();
    }
    
    private void addEvaluator(final EventEvaluator<ILoggingEvent> ee) {
        if (this.evaluatorList == null) {
            this.evaluatorList = new ArrayList<EventEvaluator<ILoggingEvent>>();
        }
        this.evaluatorList.add(ee);
    }
    
    public void stop() {
        this.evaluatorList = null;
        super.stop();
    }
    
    protected void extraData(final StringBuilder builder, final StackTraceElementProxy step) {
    }
    
    public String convert(final ILoggingEvent event) {
        final IThrowableProxy tp = event.getThrowableProxy();
        if (tp == null) {
            return "";
        }
        if (this.evaluatorList != null) {
            boolean printStack = true;
            for (int i = 0; i < this.evaluatorList.size(); ++i) {
                final EventEvaluator<ILoggingEvent> ee = this.evaluatorList.get(i);
                try {
                    if (ee.evaluate(event)) {
                        printStack = false;
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
            if (!printStack) {
                return "";
            }
        }
        return this.throwableProxyToString(tp);
    }
    
    protected String throwableProxyToString(final IThrowableProxy tp) {
        final StringBuilder buf = new StringBuilder(32);
        for (IThrowableProxy currentThrowable = tp; currentThrowable != null; currentThrowable = currentThrowable.getCause()) {
            this.subjoinThrowableProxy(buf, currentThrowable);
        }
        return buf.toString();
    }
    
    void subjoinThrowableProxy(final StringBuilder buf, final IThrowableProxy tp) {
        ThrowableProxyUtil.subjoinFirstLine(buf, tp);
        buf.append(CoreConstants.LINE_SEPARATOR);
        final StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        final int commonFrames = tp.getCommonFrames();
        final boolean unrestrictedPrinting = this.lengthOption > stepArray.length;
        int maxIndex = unrestrictedPrinting ? stepArray.length : this.lengthOption;
        if (commonFrames > 0 && unrestrictedPrinting) {
            maxIndex -= commonFrames;
        }
        for (int i = 0; i < maxIndex; ++i) {
            final String string = stepArray[i].toString();
            buf.append('\t');
            buf.append(string);
            this.extraData(buf, stepArray[i]);
            buf.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0 && unrestrictedPrinting) {
            buf.append("\t... ").append(tp.getCommonFrames()).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }
}
