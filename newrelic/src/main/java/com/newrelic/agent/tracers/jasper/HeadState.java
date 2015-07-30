// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import com.newrelic.agent.Transaction;

public class HeadState extends AbstractRUMState
{
    public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        final Matcher scriptMatcher = HeadState.SCRIPT_PATTERN.matcher(text);
        Integer scriptIndex = null;
        if (scriptMatcher.find()) {
            scriptIndex = scriptMatcher.end();
        }
        Matcher matcher = HeadState.HEAD_PATTERN.matcher(text);
        if (matcher.find()) {
            if (this.isScriptFirst(scriptIndex, matcher.end())) {
                return HeadState.PRE_HEAD_SCRIPT_STATE.process(tx, generator, node, text);
            }
            String s = text.substring(0, matcher.end());
            this.writeText(tx, generator, node, s);
            s = text.substring(matcher.end());
            return HeadState.PRE_META_STATE.process(tx, generator, node, s);
        }
        else {
            matcher = HeadState.HEAD_END_PATTERN.matcher(text);
            if (matcher.find()) {
                if (this.isScriptFirst(scriptIndex, matcher.end())) {
                    String s = text.substring(0, scriptMatcher.start());
                    this.writeText(tx, generator, node, s);
                    this.writeHeader(generator);
                    s = text.substring(scriptMatcher.start());
                    return HeadState.SCRIPT_STATE.process(tx, generator, node, s);
                }
                String s = text.substring(0, matcher.start());
                this.writeText(tx, generator, node, s);
                this.writeHeader(generator);
                s = text.substring(matcher.start());
                return HeadState.BODY_STATE.process(tx, generator, node, s);
            }
            else {
                matcher = HeadState.BODY_END_PATTERN.matcher(text);
                if (matcher.find()) {
                    if (this.isScriptFirst(scriptIndex, matcher.end())) {
                        return HeadState.SCRIPT_STATE.process(tx, generator, node, text);
                    }
                    return HeadState.BODY_STATE.process(tx, generator, node, text);
                }
                else {
                    matcher = HeadState.BODY_START_PATTERN.matcher(text);
                    if (scriptIndex == null) {
                        this.writeText(tx, generator, node, text);
                        return this;
                    }
                    if (!matcher.find()) {
                        return HeadState.PRE_HEAD_SCRIPT_STATE.process(tx, generator, node, text);
                    }
                    if (this.isScriptFirst(scriptIndex, matcher.end())) {
                        return HeadState.PRE_HEAD_SCRIPT_STATE.process(tx, generator, node, text);
                    }
                    return HeadState.SCRIPT_STATE.process(tx, generator, node, text);
                }
            }
        }
    }
    
    private boolean isScriptFirst(final Integer scriptIndex, final Integer headIndex) {
        return scriptIndex != null && (headIndex == null || headIndex > scriptIndex);
    }
}
