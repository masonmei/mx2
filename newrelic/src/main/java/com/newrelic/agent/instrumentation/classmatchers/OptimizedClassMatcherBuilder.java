// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Iterator;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.methodmatchers.AnnotationMethodMatcher;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Multimaps;
import java.util.Collection;
import java.util.HashMap;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.deps.com.google.common.collect.SetMultimap;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.base.Supplier;

public class OptimizedClassMatcherBuilder
{
    private static Supplier<Set<ClassAndMethodMatcher>> CLASS_AND_METHOD_MATCHER_SET_SUPPLIER;
    private final SetMultimap<MethodMatcher, ClassAndMethodMatcher> methodMatchers;
    private final SetMultimap<Method, ClassAndMethodMatcher> methods;
    private final Set<String> methodAnnotationMatchers;
    private final Set<String> exactClassNames;
    private boolean exactClassMatch;
    
    private OptimizedClassMatcherBuilder() {
        this.methodMatchers = Multimaps.newSetMultimap(new HashMap<MethodMatcher, Collection<ClassAndMethodMatcher>>(), OptimizedClassMatcherBuilder.CLASS_AND_METHOD_MATCHER_SET_SUPPLIER);
        this.methods = Multimaps.newSetMultimap(new HashMap<Method, Collection<ClassAndMethodMatcher>>(), OptimizedClassMatcherBuilder.CLASS_AND_METHOD_MATCHER_SET_SUPPLIER);
        this.methodAnnotationMatchers = (Set<String>)Sets.newHashSet();
        this.exactClassNames = (Set<String>)Sets.newHashSet();
        this.exactClassMatch = true;
    }
    
    public static OptimizedClassMatcherBuilder newBuilder() {
        return new OptimizedClassMatcherBuilder();
    }
    
    public OptimizedClassMatcherBuilder addClassMethodMatcher(final ClassAndMethodMatcher... matchers) {
        for (final ClassAndMethodMatcher matcher : matchers) {
            if (this.exactClassMatch && !matcher.getClassMatcher().isExactClassMatcher()) {
                this.exactClassMatch = false;
            }
            else {
                this.exactClassNames.addAll(matcher.getClassMatcher().getClassNames());
            }
            if (matcher.getMethodMatcher() instanceof AnnotationMethodMatcher) {
                this.methodAnnotationMatchers.add(((AnnotationMethodMatcher)matcher.getMethodMatcher()).getAnnotationType().getDescriptor());
            }
            final Method[] exactMethods = matcher.getMethodMatcher().getExactMethods();
            if (exactMethods == null || exactMethods.length == 0) {
                this.methodMatchers.put(matcher.getMethodMatcher(), matcher);
            }
            else {
                for (final Method m : exactMethods) {
                    if (OptimizedClassMatcher.METHODS_WE_NEVER_INSTRUMENT.contains(m)) {
                        Agent.LOG.severe("Skipping method matcher for method " + m);
                        Agent.LOG.fine("Skipping matcher for class matcher " + matcher.getClassMatcher());
                    }
                    else {
                        if (OptimizedClassMatcher.DEFAULT_CONSTRUCTOR.equals(m)) {
                            Agent.LOG.severe("Instrumentation is matching a default constructor.  This may result in slow class loading times.");
                            Agent.LOG.debug("No arg constructor matcher: " + matcher.getClassMatcher());
                        }
                        this.methods.put(m, matcher);
                    }
                }
            }
        }
        return this;
    }
    
    public OptimizedClassMatcherBuilder copyFrom(final ClassMatchVisitorFactory otherMatcher) {
        if (otherMatcher instanceof OptimizedClassMatcher) {
            final OptimizedClassMatcher matcher = (OptimizedClassMatcher)otherMatcher;
            this.methodAnnotationMatchers.addAll(matcher.methodAnnotationsToMatch);
            for (final Map.Entry<MethodMatcher, ClassAndMethodMatcher> entry : matcher.methodMatchers) {
                this.methodMatchers.put(entry.getKey(), entry.getValue());
            }
            for (final Map.Entry<Method, Collection<ClassAndMethodMatcher>> entry2 : matcher.methods.entrySet()) {
                this.methods.putAll(entry2.getKey(), (Iterable<?>)entry2.getValue());
            }
            if (null != matcher.exactClassNames) {
                this.exactClassNames.addAll(matcher.exactClassNames);
            }
            return this;
        }
        throw new UnsupportedOperationException("Unable to copy unexpected type " + otherMatcher.getClass().getName());
    }
    
    public ClassMatchVisitorFactory build() {
        if (this.methodMatchers.isEmpty() && this.methods.isEmpty() && this.methodAnnotationMatchers.isEmpty()) {
            Agent.LOG.finest("Creating an empty class/method matcher");
            return OptimizedClassMatcher.EMPTY_MATCHER;
        }
        Set<String> exactClassNames = null;
        if (this.exactClassMatch) {
            exactClassNames = this.exactClassNames;
        }
        return new OptimizedClassMatcher(this.methodAnnotationMatchers, this.methods, this.methodMatchers, exactClassNames);
    }
    
    static {
        OptimizedClassMatcherBuilder.CLASS_AND_METHOD_MATCHER_SET_SUPPLIER = new Supplier<Set<ClassAndMethodMatcher>>() {
            public Set<ClassAndMethodMatcher> get() {
                return Sets.newSetFromMap(new HashMap<ClassAndMethodMatcher, Boolean>());
            }
        };
    }
}
