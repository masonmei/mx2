// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

public class RuleResult
{
    public static final RuleResult NO_MATCH_RULE_RESULT;
    public static final RuleResult IGNORE_MATCH_RULE_RESULT;
    private final boolean isIgnore;
    private final boolean isMatch;
    private final String replacement;
    
    private RuleResult(final boolean isIgnore, final boolean isMatch, final String replacement) {
        this.isIgnore = isIgnore;
        this.isMatch = isMatch;
        this.replacement = replacement;
    }
    
    public boolean isIgnore() {
        return this.isIgnore;
    }
    
    public boolean isMatch() {
        return this.isMatch;
    }
    
    public String getReplacement() {
        return this.replacement;
    }
    
    public static RuleResult getIgnoreMatch() {
        return RuleResult.IGNORE_MATCH_RULE_RESULT;
    }
    
    public static RuleResult getNoMatch() {
        return RuleResult.NO_MATCH_RULE_RESULT;
    }
    
    public static RuleResult getMatch(final String replacement) {
        return new RuleResult(false, true, replacement);
    }
    
    static {
        NO_MATCH_RULE_RESULT = new RuleResult(false, false, null);
        IGNORE_MATCH_RULE_RESULT = new RuleResult(true, true, null);
    }
}
