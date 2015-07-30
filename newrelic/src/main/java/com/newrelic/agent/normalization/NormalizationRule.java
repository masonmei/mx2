// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import java.text.MessageFormat;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NormalizationRule
{
    private static final Pattern SEGMENT_SEPARATOR_PATTERN;
    private static final Pattern BACKREFERENCE_PATTERN;
    private static final String BACKREFERENCE_REPLACEMENT = "\\$$1";
    private final Pattern pattern;
    private final boolean ignore;
    private final boolean terminateChain;
    private final int order;
    private final boolean eachSegment;
    private final boolean replaceAll;
    private final String replaceRegex;
    private final ReplacementFormatter formatter;
    
    public NormalizationRule(final String matchExp, final String replacement, final boolean ignore, final int order, final boolean terminateChain, final boolean eachSegment, final boolean replaceAll) throws PatternSyntaxException {
        this.ignore = ignore;
        this.order = order;
        this.terminateChain = terminateChain;
        this.eachSegment = eachSegment;
        this.replaceAll = replaceAll;
        this.pattern = Pattern.compile(matchExp, 34);
        if (replacement == null || replacement.length() == 0) {
            this.replaceRegex = null;
        }
        else {
            final Matcher backReferenceMatcher = NormalizationRule.BACKREFERENCE_PATTERN.matcher(replacement);
            this.replaceRegex = backReferenceMatcher.replaceAll("\\$$1");
        }
        if (ignore) {
            this.formatter = new IgnoreReplacementFormatter();
        }
        else {
            this.formatter = new FancyReplacementFormatter();
        }
    }
    
    public boolean isTerminateChain() {
        return this.terminateChain;
    }
    
    public RuleResult normalize(final String name) {
        return this.formatter.getRuleResult(name);
    }
    
    public boolean isIgnore() {
        return this.ignore;
    }
    
    public boolean isReplaceAll() {
        return this.replaceAll;
    }
    
    public boolean isEachSegment() {
        return this.eachSegment;
    }
    
    public String getReplacement() {
        return this.replaceRegex;
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public String getMatchExpression() {
        return this.pattern.pattern();
    }
    
    public String toString() {
        return MessageFormat.format("match_expression: {0} replacement: {1} eval_order: {2} each_segment: {3} ignore: {4} terminate_chain: {5} replace_all: {6}", this.pattern.pattern(), this.replaceRegex, this.order, this.eachSegment, this.ignore, this.terminateChain, this.replaceAll);
    }
    
    static {
        SEGMENT_SEPARATOR_PATTERN = Pattern.compile("/");
        BACKREFERENCE_PATTERN = Pattern.compile("\\\\(\\d)");
    }
    
    private class IgnoreReplacementFormatter implements ReplacementFormatter
    {
        public RuleResult getRuleResult(final String url) {
            if (NormalizationRule.this.eachSegment) {
                return this.forEachSegment(url);
            }
            return this.forEntireUrl(url);
        }
        
        private RuleResult forEachSegment(final String url) {
            final String[] segments = NormalizationRule.SEGMENT_SEPARATOR_PATTERN.split(url);
            for (int i = 1; i < segments.length; ++i) {
                final String segment = segments[i];
                if (segment != null) {
                    if (segment.length() != 0) {
                        final Matcher matcher = NormalizationRule.this.pattern.matcher(segment);
                        if (matcher.find()) {
                            return RuleResult.getIgnoreMatch();
                        }
                    }
                }
            }
            return RuleResult.getNoMatch();
        }
        
        private RuleResult forEntireUrl(final String url) {
            final Matcher matcher = NormalizationRule.this.pattern.matcher(url);
            return matcher.find() ? RuleResult.getIgnoreMatch() : RuleResult.getNoMatch();
        }
    }
    
    private class FancyReplacementFormatter implements ReplacementFormatter
    {
        public RuleResult getRuleResult(final String url) {
            if (NormalizationRule.this.eachSegment) {
                return this.forEachSegment(url);
            }
            return this.forEntireUrl(url);
        }
        
        private RuleResult forEachSegment(final String url) {
            boolean isMatch = false;
            final String[] segments = NormalizationRule.SEGMENT_SEPARATOR_PATTERN.split(url);
            for (int i = 1; i < segments.length; ++i) {
                final String segment = segments[i];
                if (segment != null) {
                    if (segment.length() != 0) {
                        final RuleResult result = this.forEntireUrl(segment);
                        if (result.isMatch()) {
                            isMatch = true;
                            segments[i] = result.getReplacement();
                        }
                    }
                }
            }
            if (!isMatch) {
                return RuleResult.getNoMatch();
            }
            final StringBuilder path = new StringBuilder();
            for (int j = 1; j < segments.length; ++j) {
                final String segment2 = segments[j];
                if (segment2 != null) {
                    if (segment2.length() != 0) {
                        path.append('/').append(segment2);
                    }
                }
            }
            return RuleResult.getMatch(path.toString());
        }
        
        private RuleResult forEntireUrl(final String url) {
            final Matcher matcher = NormalizationRule.this.pattern.matcher(url);
            if (matcher.find()) {
                String replacement;
                if (NormalizationRule.this.replaceRegex == null || NormalizationRule.this.replaceRegex.length() == 0) {
                    replacement = null;
                }
                else if (NormalizationRule.this.replaceAll) {
                    replacement = matcher.replaceAll(NormalizationRule.this.replaceRegex);
                }
                else {
                    replacement = matcher.replaceFirst(NormalizationRule.this.replaceRegex);
                }
                return RuleResult.getMatch(replacement);
            }
            return RuleResult.getNoMatch();
        }
    }
    
    private interface ReplacementFormatter
    {
        RuleResult getRuleResult(String p0);
    }
}
