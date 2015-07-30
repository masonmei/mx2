// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import com.newrelic.agent.Transaction;

public class TitleState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        Matcher matcher = TitleState.TITLE_END.matcher(text);
        if (matcher.find()) {
            String s = text.substring(0, matcher.end());
            this.writeText(tx, generator, node, s);
            s = text.substring(matcher.end());
            return TitleState.PRE_META_STATE.process(tx, generator, node, s);
        }
        matcher = TitleState.HEAD_END_PATTERN.matcher(text);
        if (matcher.find()) {
            String s = text.substring(0, matcher.start());
            this.writeText(tx, generator, node, s);
            this.writeHeader(generator);
            s = text.substring(matcher.start());
            return TitleState.BODY_STATE.process(tx, generator, node, s);
        }
        this.writeText(tx, generator, node, text);
        return this;
    }
}
