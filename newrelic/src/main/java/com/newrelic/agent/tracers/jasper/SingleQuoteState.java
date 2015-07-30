// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import com.newrelic.agent.Transaction;

public class SingleQuoteState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        final Matcher nextQuoteMatcher = SingleQuoteState.SINGLE_QUOTE_PATTERN.matcher(text);
        if (nextQuoteMatcher.find()) {
            String s = text.substring(0, nextQuoteMatcher.end());
            this.writeText(tx, generator, node, s);
            s = text.substring(nextQuoteMatcher.end());
            return SingleQuoteState.META_STATE.process(tx, generator, node, s);
        }
        final Matcher matcher = SingleQuoteState.HEAD_END_PATTERN.matcher(text);
        if (matcher.find()) {
            String s2 = text.substring(0, matcher.start());
            this.writeText(tx, generator, node, s2);
            this.writeHeader(generator);
            s2 = text.substring(matcher.start());
            return SingleQuoteState.BODY_STATE.process(tx, generator, node, s2);
        }
        this.writeText(tx, generator, node, text);
        return this;
    }
}
