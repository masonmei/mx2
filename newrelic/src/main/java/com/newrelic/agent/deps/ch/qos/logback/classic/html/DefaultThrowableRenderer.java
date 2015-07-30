// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.html;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.core.helpers.Transform;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.IThrowableProxy;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.html.IThrowableRenderer;

public class DefaultThrowableRenderer implements IThrowableRenderer<ILoggingEvent>
{
    static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";
    
    public void render(final StringBuilder sbuf, final ILoggingEvent event) {
        IThrowableProxy tp = event.getThrowableProxy();
        sbuf.append("<tr><td class=\"Exception\" colspan=\"6\">");
        while (tp != null) {
            this.render(sbuf, tp);
            tp = tp.getCause();
        }
        sbuf.append("</td></tr>");
    }
    
    void render(final StringBuilder sbuf, final IThrowableProxy tp) {
        this.printFirstLine(sbuf, tp);
        final int commonFrames = tp.getCommonFrames();
        final StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        for (int i = 0; i < stepArray.length - commonFrames; ++i) {
            final StackTraceElementProxy step = stepArray[i];
            sbuf.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;");
            sbuf.append(Transform.escapeTags(step.toString()));
            sbuf.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0) {
            sbuf.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;");
            sbuf.append("\t... " + commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }
    
    public void printFirstLine(final StringBuilder sb, final IThrowableProxy tp) {
        final int commonFrames = tp.getCommonFrames();
        if (commonFrames > 0) {
            sb.append("<br />").append("Caused by: ");
        }
        sb.append(tp.getClassName()).append(": ").append(Transform.escapeTags(tp.getMessage()));
        sb.append(CoreConstants.LINE_SEPARATOR);
    }
}
