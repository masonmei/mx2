// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Collections;
import java.util.Collection;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;

public class NoMatchMatcher extends ClassMatcher
{
    public static final ClassMatcher MATCHER;
    
    public boolean isMatch(final ClassLoader loader, final ClassReader cr) {
        return false;
    }
    
    public boolean isMatch(final Class<?> clazz) {
        return false;
    }
    
    public Collection<String> getClassNames() {
        return Collections.emptyList();
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && this.getClass() == obj.getClass());
    }
    
    static {
        MATCHER = new NoMatchMatcher();
    }
}
