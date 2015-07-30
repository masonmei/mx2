// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.custom;

import java.util.Collection;
import java.util.List;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import java.util.Set;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;

public class ClassRetransformer
{
    private final InstrumentationContextManager contextManager;
    private final Set<ClassMatchVisitorFactory> matchers;
    private CustomClassTransformer transformer;
    
    public ClassRetransformer(final InstrumentationContextManager contextManager) {
        this.contextManager = contextManager;
        this.matchers = (Set<ClassMatchVisitorFactory>)Sets.newHashSet();
    }
    
    public synchronized void setClassMethodMatchers(final List<ExtensionClassAndMethodMatcher> newMatchers) {
        this.matchers.clear();
        if (this.transformer != null) {
            this.matchers.add(this.transformer.getMatcher());
            this.transformer.destroy();
        }
        if (newMatchers.isEmpty()) {
            this.transformer = null;
        }
        else {
            this.transformer = new CustomClassTransformer(this.contextManager, newMatchers);
            this.matchers.add(this.transformer.getMatcher());
        }
    }
    
    public synchronized void appendClassMethodMatchers(final List<ExtensionClassAndMethodMatcher> toAdd) {
        if (this.transformer != null) {
            toAdd.addAll(this.transformer.extensionPointCuts);
        }
        this.setClassMethodMatchers(toAdd);
    }
    
    public Set<ClassMatchVisitorFactory> getMatchers() {
        return this.matchers;
    }
}
