// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Collections;
import java.util.Collection;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;

public class AllClassesMatcher extends ClassMatcher
{
    public boolean isMatch(final ClassLoader loader, final ClassReader cr) {
        return (cr.getAccess() & 0x200) == 0x0;
    }
    
    public boolean isMatch(final Class<?> clazz) {
        return !clazz.isInterface();
    }
    
    public Collection<String> getClassNames() {
        return (Collection<String>)Collections.emptyList();
    }
}
