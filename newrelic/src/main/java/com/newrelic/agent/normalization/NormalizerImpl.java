// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import java.util.Iterator;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Collections;
import java.util.List;

public class NormalizerImpl implements Normalizer
{
    private final List<NormalizationRule> rules;
    private final String appName;
    
    public NormalizerImpl(final String appName, final List<NormalizationRule> rules) {
        this.appName = appName;
        this.rules = Collections.unmodifiableList((List<? extends NormalizationRule>)rules);
    }
    
    public String normalize(final String name) {
        if (name == null) {
            return null;
        }
        String normalizedName = name;
        for (final NormalizationRule rule : this.rules) {
            final RuleResult result = rule.normalize(normalizedName);
            if (!result.isMatch()) {
                continue;
            }
            if (rule.isIgnore()) {
                if (Agent.LOG.isLoggable(Level.FINER)) {
                    final String msg = MessageFormat.format("Ignoring \"{0}\" for \"{1}\" because it matched rule \"{2}\"", name, this.appName, rule);
                    Agent.LOG.finer(msg);
                }
                return null;
            }
            final String replacement = result.getReplacement();
            if (replacement != null) {
                if (Agent.LOG.isLoggable(Level.FINER)) {
                    final String msg2 = MessageFormat.format("Normalized \"{0}\" to \"{2}\" for \"{1}\" using rule \"{3}\"", name, this.appName, replacement, rule);
                    Agent.LOG.finer(msg2);
                }
                normalizedName = replacement;
            }
            if (rule.isTerminateChain()) {
                break;
            }
        }
        return normalizedName;
    }
    
    public List<NormalizationRule> getRules() {
        return this.rules;
    }
}
