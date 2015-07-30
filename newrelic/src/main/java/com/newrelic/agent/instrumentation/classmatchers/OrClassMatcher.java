// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import com.newrelic.agent.util.Strings;
import java.util.HashSet;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

public class OrClassMatcher extends ManyClassMatcher
{
    public OrClassMatcher(final ClassMatcher... matchers) {
        this(Arrays.asList(matchers));
    }
    
    public OrClassMatcher(final Collection<ClassMatcher> matchers) {
        super(getOptimizedClassMatchers(matchers));
    }
    
    private static Collection<ClassMatcher> getOptimizedClassMatchers(final Collection<ClassMatcher> matchers) {
        final Collection<String> exactMatcherClassNames = new ArrayList<String>(matchers.size());
        final Collection<ClassMatcher> otherMatchers = new LinkedList<ClassMatcher>();
        for (final ClassMatcher matcher : matchers) {
            if (matcher instanceof ExactClassMatcher) {
                exactMatcherClassNames.add(((ExactClassMatcher)matcher).getInternalClassName());
            }
            else {
                otherMatchers.add(matcher);
            }
        }
        if (exactMatcherClassNames.size() <= 1) {
            return matchers;
        }
        otherMatchers.add(createClassMatcher((String[])exactMatcherClassNames.toArray(new String[exactMatcherClassNames.size()])));
        return otherMatchers;
    }
    
    public boolean isMatch(final ClassLoader loader, final ClassReader cr) {
        for (final ClassMatcher matcher : this.getClassMatchers()) {
            if (matcher.isMatch(loader, cr)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isMatch(final Class<?> clazz) {
        for (final ClassMatcher matcher : this.getClassMatchers()) {
            if (matcher.isMatch(clazz)) {
                return true;
            }
        }
        return false;
    }
    
    public static ClassMatcher getClassMatcher(final ClassMatcher... classMatchers) {
        return getClassMatcher(Arrays.asList(classMatchers));
    }
    
    public static ClassMatcher getClassMatcher(Collection<ClassMatcher> classMatchers) {
        classMatchers = getOptimizedClassMatchers(classMatchers);
        if (classMatchers.size() == 0) {
            return new NoMatchMatcher();
        }
        if (classMatchers.size() == 1) {
            return classMatchers.iterator().next();
        }
        return new OrClassMatcher(classMatchers);
    }
    
    static ClassMatcher createClassMatcher(final String... classNames) {
        if (classNames.length == 0) {
            return NoMatchMatcher.MATCHER;
        }
        if (classNames.length == 1) {
            return new ExactClassMatcher(classNames[0]);
        }
        return new StringOrClassMatcher(classNames);
    }
    
    private static class StringOrClassMatcher extends ClassMatcher
    {
        private final Set<String> internalClassNames;
        private final Set<String> classNames;
        
        public StringOrClassMatcher(final String... internalClassNames) {
            this.internalClassNames = new HashSet<String>();
            for (final String name : internalClassNames) {
                this.internalClassNames.add(Strings.fixInternalClassName(name));
            }
            this.classNames = new HashSet<String>();
            for (final String name2 : this.internalClassNames) {
                this.classNames.add(name2.replace('/', '.'));
            }
        }
        
        public boolean isMatch(final ClassLoader loader, final ClassReader cr) {
            return this.internalClassNames.contains(cr.getClassName());
        }
        
        public boolean isMatch(final Class<?> clazz) {
            return this.classNames.contains(clazz.getName());
        }
        
        public String toString() {
            return this.classNames.toString();
        }
        
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.internalClassNames == null) ? 0 : this.internalClassNames.hashCode());
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
            final StringOrClassMatcher other = (StringOrClassMatcher)obj;
            if (this.internalClassNames == null) {
                if (other.internalClassNames != null) {
                    return false;
                }
            }
            else if (!this.internalClassNames.equals(other.internalClassNames)) {
                return false;
            }
            return true;
        }
        
        public boolean isExactClassMatcher() {
            return true;
        }
        
        public Collection<String> getClassNames() {
            return this.internalClassNames;
        }
    }
}
