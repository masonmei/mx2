// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

public abstract class ManyClassMatcher extends ClassMatcher
{
    private final ClassMatcher[] matchers;
    private final boolean isExact;
    
    public ManyClassMatcher(final Collection<ClassMatcher> matchers) {
        this.matchers = matchers.toArray(new ClassMatcher[matchers.size()]);
        this.isExact = determineIfExact(this.matchers);
    }
    
    private static boolean determineIfExact(final ClassMatcher[] matchers) {
        for (final ClassMatcher matcher : matchers) {
            if (!matcher.isExactClassMatcher()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isExactClassMatcher() {
        return this.isExact;
    }
    
    protected ClassMatcher[] getClassMatchers() {
        return this.matchers;
    }
    
    public Collection<String> getClassNames() {
        final Collection<String> classNames = new ArrayList<String>();
        for (final ClassMatcher matcher : this.matchers) {
            classNames.addAll(matcher.getClassNames());
        }
        return classNames;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.matchers);
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
        final ManyClassMatcher other = (ManyClassMatcher)obj;
        return Arrays.equals(this.matchers, other.matchers);
    }
    
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.matchers + ")";
    }
}
