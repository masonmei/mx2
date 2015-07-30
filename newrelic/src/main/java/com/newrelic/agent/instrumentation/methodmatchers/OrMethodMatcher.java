// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import java.util.HashSet;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

public final class OrMethodMatcher extends ManyMethodMatcher
{
    private OrMethodMatcher(final MethodMatcher... methodMatchers) {
        super(methodMatchers);
    }
    
    private OrMethodMatcher(final Collection<MethodMatcher> methodMatchers) {
        super(methodMatchers);
    }
    
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        for (final MethodMatcher matcher : this.methodMatchers) {
            if (matcher.matches(access, name, desc, annotations)) {
                return true;
            }
        }
        return false;
    }
    
    public static final MethodMatcher getMethodMatcher(final MethodMatcher... matchers) {
        return getMethodMatcher(Arrays.asList(matchers));
    }
    
    public static final MethodMatcher getMethodMatcher(final Collection<MethodMatcher> matchers) {
        if (matchers.size() == 1) {
            return matchers.iterator().next();
        }
        final Map<String, DescMethodMatcher> exactMatchers = new HashMap<String, DescMethodMatcher>();
        final List<MethodMatcher> otherMatchers = new LinkedList<MethodMatcher>();
        for (final MethodMatcher matcher : matchers) {
            if (matcher instanceof ExactMethodMatcher) {
                final ExactMethodMatcher m = (ExactMethodMatcher)matcher;
                if (m.getDescriptions().isEmpty()) {
                    otherMatchers.add(matcher);
                }
                else {
                    DescMethodMatcher descMatcher = exactMatchers.get(m.getName());
                    if (descMatcher == null) {
                        descMatcher = new DescMethodMatcher(m.getDescriptions());
                        exactMatchers.put(m.getName().intern(), descMatcher);
                    }
                    else {
                        descMatcher.addDescriptions(m.getDescriptions());
                    }
                }
            }
            else {
                otherMatchers.add(matcher);
            }
        }
        final MethodMatcher matcher2 = new OrExactMethodMatchers(exactMatchers);
        if (otherMatchers.size() == 0) {
            return matcher2;
        }
        otherMatchers.add(matcher2);
        return new OrMethodMatcher(otherMatchers);
    }
    
    public String toString() {
        return "Or Match " + this.methodMatchers;
    }
    
    private static class OrExactMethodMatchers implements MethodMatcher
    {
        private final Map<String, DescMethodMatcher> exactMatchers;
        
        public OrExactMethodMatchers(final Map<String, DescMethodMatcher> exactMatchers) {
            this.exactMatchers = exactMatchers;
        }
        
        public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
            final DescMethodMatcher matcher = this.exactMatchers.get(name);
            return matcher != null && matcher.matches(access, name, desc, annotations);
        }
        
        public String toString() {
            return this.exactMatchers.toString();
        }
        
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.exactMatchers == null) ? 0 : this.exactMatchers.hashCode());
            return result;
        }
        
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final OrExactMethodMatchers other = (OrExactMethodMatchers)obj;
            if (this.exactMatchers == null) {
                if (other.exactMatchers != null) {
                    return false;
                }
            }
            else if (!this.exactMatchers.equals(other.exactMatchers)) {
                return false;
            }
            return true;
        }
        
        public Method[] getExactMethods() {
            final List<Method> methods = (List<Method>)Lists.newArrayList();
            for (final Map.Entry<String, DescMethodMatcher> entry : this.exactMatchers.entrySet()) {
                for (final String desc : entry.getValue().descriptions) {
                    methods.add(new Method(entry.getKey(), desc));
                }
            }
            return methods.toArray(new Method[methods.size()]);
        }
    }
    
    private static class DescMethodMatcher implements MethodMatcher
    {
        private Set<String> descriptions;
        
        public DescMethodMatcher(final Set<String> set) {
            this.descriptions = new HashSet<String>(set);
        }
        
        public void addDescriptions(final Set<String> desc) {
            this.descriptions.addAll(desc);
        }
        
        public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
            return this.descriptions.contains(desc);
        }
        
        public String toString() {
            return this.descriptions.toString();
        }
        
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.descriptions == null) ? 0 : this.descriptions.hashCode());
            return result;
        }
        
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final DescMethodMatcher other = (DescMethodMatcher)obj;
            if (this.descriptions == null) {
                if (other.descriptions != null) {
                    return false;
                }
            }
            else if (!this.descriptions.equals(other.descriptions)) {
                return false;
            }
            return true;
        }
        
        public Method[] getExactMethods() {
            return null;
        }
    }
}
