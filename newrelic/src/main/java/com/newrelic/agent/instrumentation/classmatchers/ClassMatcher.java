// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.io.IOException;
import java.util.Collection;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;

public abstract class ClassMatcher
{
    protected static final String JAVA_LANG_OBJECT_INTERNAL_NAME = "java/lang/Object";
    
    public abstract boolean isMatch(final ClassLoader p0, final ClassReader p1);
    
    public abstract boolean isMatch(final Class<?> p0);
    
    public abstract Collection<String> getClassNames();
    
    public boolean isExactClassMatcher() {
        return false;
    }
    
    public ClassMatcher getClassMatcher(final ClassLoader classLoader) throws IOException {
        return this;
    }
}
