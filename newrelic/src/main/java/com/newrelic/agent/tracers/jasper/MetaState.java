// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import com.newrelic.agent.Transaction;

public class MetaState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        final Matcher tagMatcher = MetaState.END_TAG_OR_QUOTE_PATTERN.matcher(text);
        if (!tagMatcher.find()) {
            this.writeText(tx, generator, node, text);
            return this;
        }
        if (tagMatcher.group().equals("\"")) {
            String s = text.substring(0, tagMatcher.end());
            this.writeText(tx, generator, node, s);
            s = text.substring(tagMatcher.end());
            return MetaState.QUOTE_STATE.process(tx, generator, node, s);
        }
        if (tagMatcher.group().equals("'")) {
            String s = text.substring(0, tagMatcher.end());
            this.writeText(tx, generator, node, s);
            s = text.substring(tagMatcher.end());
            return MetaState.SINGLE_QUOTE_STATE.process(tx, generator, node, s);
        }
        String s = text.substring(0, tagMatcher.start());
        this.writeText(tx, generator, node, s);
        s = text.substring(tagMatcher.start());
        return MetaState.PRE_META_STATE.process(tx, generator, node, s);
    }
}
