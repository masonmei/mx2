// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Collections;
import java.util.Collection;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;

public class NotMatcher extends ClassMatcher
{
    private final ClassMatcher matcher;
    
    public NotMatcher(final ClassMatcher notMatch) {
        this.matcher = notMatch;
    }
    
    public boolean isMatch(final ClassLoader loader, final ClassReader cr) {
        return !this.matcher.isMatch(loader, cr);
    }
    
    public boolean isMatch(final Class<?> clazz) {
        return !this.matcher.isMatch(clazz);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.matcher == null) ? 0 : this.matcher.hashCode());
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
        final NotMatcher other = (NotMatcher)obj;
        if (this.matcher == null) {
            if (other.matcher != null) {
                return false;
            }
        }
        else if (!this.matcher.equals(other.matcher)) {
            return false;
        }
        return true;
    }
    
    public Collection<String> getClassNames() {
        return (Collection<String>)Collections.emptyList();
    }
}
