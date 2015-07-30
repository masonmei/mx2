// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Arrays;
import java.util.Collection;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

public class ExactClassMatcher extends ClassMatcher
{
    private final Type type;
    private final String className;
    private final String internalName;
    
    public ExactClassMatcher(final String className) {
        this.type = Type.getObjectType(Strings.fixInternalClassName(className));
        this.className = this.type.getClassName();
        this.internalName = this.type.getInternalName();
    }
    
    public boolean isMatch(final ClassLoader loader, final ClassReader cr) {
        return cr.getClassName().equals(this.internalName);
    }
    
    public boolean isMatch(final Class<?> clazz) {
        return clazz.getName().equals(this.className);
    }
    
    public static ClassMatcher or(final String... classNames) {
        return OrClassMatcher.createClassMatcher(classNames);
    }
    
    public String getInternalClassName() {
        return this.internalName;
    }
    
    public boolean isExactClassMatcher() {
        return true;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
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
        final ExactClassMatcher other = (ExactClassMatcher)obj;
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        }
        else if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }
    
    public String toString() {
        return "ExactClassMatcher(" + this.internalName + ")";
    }
    
    public Collection<String> getClassNames() {
        return Arrays.asList(this.internalName);
    }
}
