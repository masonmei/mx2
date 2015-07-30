// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import com.newrelic.agent.Transaction;

public class BodyState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        Matcher matcher = BodyState.SCRIPT_PATTERN.matcher(text);
        if (matcher.find()) {
            final String begin = text.substring(0, matcher.start());
            final RUMState state = this.process(tx, generator, node, begin);
            final String s = text.substring(matcher.start());
            if (state == BodyState.DONE_STATE) {
                return BodyState.DONE_STATE.process(tx, generator, node, s);
            }
            return BodyState.SCRIPT_STATE.process(tx, generator, node, s);
        }
        else {
            matcher = BodyState.BODY_END_PATTERN.matcher(text);
            if (matcher.find()) {
                String s2 = text.substring(0, matcher.start());
                this.writeText(tx, generator, node, s2);
                this.writeFooter(generator);
                s2 = text.substring(matcher.start());
                return BodyState.DONE_STATE.process(tx, generator, node, s2);
            }
            matcher = BodyState.HTML_END_PATTERN.matcher(text);
            if (matcher.find()) {
                String s2 = text.substring(0, matcher.start());
                this.writeText(tx, generator, node, s2);
                this.writeFooter(generator);
                s2 = text.substring(matcher.start());
                return BodyState.DONE_STATE.process(tx, generator, node, s2);
            }
            this.writeText(tx, generator, node, text);
            return this;
        }
    }
}
