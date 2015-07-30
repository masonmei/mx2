// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;

class AgentClassWriter extends ClassWriter
{
    private ClassLoader classLoader;
    
    public AgentClassWriter(final ClassReader classReader, final int flags, final ClassLoader loader) {
        super(classReader, flags);
        this.classLoader = loader;
    }
    
    protected String getCommonSuperClass(final String type1, final String type2) {
        ClassMetadata c1 = new ClassMetadata(type1, this.classLoader);
        final ClassMetadata c2 = new ClassMetadata(type2, this.classLoader);
        if (c1.isAssignableFrom(c2)) {
            return type1;
        }
        if (c2.isAssignableFrom(c1)) {
            return type2;
        }
        if (c1.isInterface() || c2.isInterface()) {
            return "java/lang/Object";
        }
        do {
            c1 = c1.getSuperclass();
        } while (!c1.isAssignableFrom(c2));
        return c1.getName().replace('.', '/');
    }
}
