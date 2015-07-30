// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ThrowableProxyUtil;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.IThrowableProxy;

public class RootCauseFirstThrowableProxyConverter extends ExtendedThrowableProxyConverter
{
    protected String throwableProxyToString(final IThrowableProxy tp) {
        final StringBuilder buf = new StringBuilder(2048);
        this.subjoinRootCauseFirst(tp, buf);
        return buf.toString();
    }
    
    private void subjoinRootCauseFirst(final IThrowableProxy tp, final StringBuilder buf) {
        if (tp.getCause() != null) {
            this.subjoinRootCauseFirst(tp.getCause(), buf);
        }
        this.subjoinRootCause(tp, buf);
    }
    
    private void subjoinRootCause(final IThrowableProxy tp, final StringBuilder buf) {
        ThrowableProxyUtil.subjoinFirstLineRootCauseFirst(buf, tp);
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
    }
}
