// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Collection;
import java.util.Arrays;

public class AndClassMatcher extends ManyClassMatcher
{
    public AndClassMatcher(final ClassMatcher... matchers) {
        this(Arrays.asList(matchers));
    }
    
    public AndClassMatcher(final Collection<ClassMatcher> matchers) {
        super(matchers);
    }
    
    public static ClassMatcher getClassMatcher(final ClassMatcher... classMatchers) {
        if (classMatchers.length == 0) {
            return new NoMatchMatcher();
        }
        if (classMatchers.length == 1) {
            return classMatchers[0];
        }
        return new AndClassMatcher(classMatchers);
    }
    
    public boolean isMatch(final ClassLoader loader, final ClassReader cr) {
        for (final ClassMatcher matcher : this.getClassMatchers()) {
            if (!matcher.isMatch(loader, cr)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isMatch(final Class<?> clazz) {
        for (final ClassMatcher matcher : this.getClassMatchers()) {
            if (!matcher.isMatch(clazz)) {
                return false;
            }
        }
        return true;
    }
}
