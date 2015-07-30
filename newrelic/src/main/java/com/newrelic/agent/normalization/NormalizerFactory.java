// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.normalization;

import java.util.Collections;
import java.util.Iterator;
import com.newrelic.agent.deps.com.google.common.base.Joiner;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;

public class NormalizerFactory
{
    public static Normalizer createUrlNormalizer(final String appName, final List<NormalizationRule> urlRules) {
        return new UrlNormalizer((Normalizer)new NormalizerImpl(appName, urlRules));
    }
    
    public static Normalizer createTransactionNormalizer(final String appName, final List<NormalizationRule> transactionNameRules, final List<TransactionSegmentTerms> transactionSegmentTermRules) {
        Normalizer normalizer = new NormalizerImpl(appName, transactionNameRules);
        if (!transactionSegmentTermRules.isEmpty()) {
            normalizer = compoundNormalizer(normalizer, createTransactionSegmentNormalizer(transactionSegmentTermRules));
        }
        return normalizer;
    }
    
    private static Normalizer compoundNormalizer(final Normalizer... normalizers) {
        final List<NormalizationRule> rules = (List<NormalizationRule>)Lists.newArrayList();
        for (final Normalizer n : normalizers) {
            rules.addAll(n.getRules());
        }
        return new Normalizer() {
            public String normalize(String name) {
                for (final Normalizer n : normalizers) {
                    name = n.normalize(name);
                    if (name == null) {
                        return name;
                    }
                }
                return name;
            }
            
            public List<NormalizationRule> getRules() {
                return rules;
            }
        };
    }
    
    static Normalizer createTransactionSegmentNormalizer(final List<TransactionSegmentTerms> transactionSegmentTermRules) {
        return new Normalizer() {
            public String normalize(String name) {
                for (final TransactionSegmentTerms terms : transactionSegmentTermRules) {
                    if (name.startsWith(terms.prefix)) {
                        final String afterPrefix = name.substring(terms.prefix.length() + 1);
                        final String[] segments = afterPrefix.split("/");
                        final List<String> keep = (List<String>)Lists.newArrayListWithCapacity(segments.length + 1);
                        keep.add(terms.prefix);
                        boolean discarded = false;
                        for (final String segment : segments) {
                            if (terms.terms.contains(segment)) {
                                keep.add(segment);
                                discarded = false;
                            }
                            else if (!discarded) {
                                keep.add("*");
                                discarded = true;
                            }
                        }
                        name = Joiner.on('/').join(keep);
                    }
                }
                return name;
            }
            
            public List<NormalizationRule> getRules() {
                return Collections.emptyList();
            }
        };
    }
    
    public static Normalizer createMetricNormalizer(final String appName, final List<NormalizationRule> metricNameRules) {
        return new NormalizerImpl(appName, metricNameRules);
    }
    
    private static class UrlNormalizer implements Normalizer
    {
        private final Normalizer normalizer;
        
        private UrlNormalizer(final Normalizer normalizer) {
            this.normalizer = normalizer;
        }
        
        public String normalize(final String name) {
            if (name == null) {
                return null;
            }
            String normalizedName = name;
            if (!normalizedName.startsWith("/")) {
                normalizedName = "/" + name;
            }
            return this.normalizer.normalize(normalizedName);
        }
        
        public List<NormalizationRule> getRules() {
            return this.normalizer.getRules();
        }
    }
}
