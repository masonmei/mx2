// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import com.newrelic.agent.Transaction;

public class PreMetaState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        final Matcher tagMatcher = PreMetaState.START_TAG_PATTERN.matcher(text);
        if (!tagMatcher.find()) {
            this.writeText(tx, generator, node, text);
            return this;
        }
        final String begin = text.substring(0, tagMatcher.start());
        this.writeText(tx, generator, node, begin);
        final String s = text.substring(tagMatcher.start());
        if (s.startsWith("</head>")) {
            this.writeHeader(generator);
            return PreMetaState.BODY_STATE.process(tx, generator, node, s);
        }
        if (s.startsWith("<meta ") || s.startsWith("<META")) {
            return PreMetaState.META_STATE.process(tx, generator, node, s);
        }
        if (s.startsWith("<title>")) {
            return PreMetaState.TITLE_STATE.process(tx, generator, node, s);
        }
        if (s.startsWith("<!--")) {
            return PreMetaState.COMMENT_STATE.process(tx, generator, node, s);
        }
        this.writeHeader(generator);
        return PreMetaState.BODY_STATE.process(tx, generator, node, s);
    }
}
