// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import com.newrelic.agent.Transaction;

public class CommentState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        final Matcher commentMatcher = CommentState.END_COMMENT.matcher(text);
        if (commentMatcher.find()) {
            String s = text.substring(0, commentMatcher.start());
            this.writeText(tx, generator, node, s);
            s = text.substring(commentMatcher.start());
            return CommentState.PRE_META_STATE.process(tx, generator, node, s);
        }
        this.writeText(tx, generator, node, text);
        return this;
    }
}
