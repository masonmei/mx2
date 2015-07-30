// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.util.asm.ClassResolvers;
import java.io.IOException;
import java.io.InputStream;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.api.agent.Logger;
import com.newrelic.agent.util.asm.ClassResolver;

public class AgentClassStructureResolver extends ClassStructureResolver
{
    private static final ClassResolver EMBEDDED_CLASS_RESOLVER;
    private final ClassResolver embeddedClassResolver;
    
    public AgentClassStructureResolver() {
        this(AgentClassStructureResolver.EMBEDDED_CLASS_RESOLVER);
    }
    
    AgentClassStructureResolver(final ClassResolver embeddedClassResolver) {
        this.embeddedClassResolver = embeddedClassResolver;
    }
    
    public ClassStructure getClassStructure(final Logger logger, final ClassLoader loader, final String internalName, final int flags) throws IOException {
        final InputStream classResource = this.embeddedClassResolver.getClassResource(internalName);
        if (classResource != null) {
            try {
                final ClassStructure classStructure = ClassStructure.getClassStructure(new ClassReader(classResource), flags);
                classResource.close();
                return classStructure;
            }
            finally {
                classResource.close();
            }
        }
        return super.getClassStructure(logger, loader, internalName, flags);
    }
    
    static {
        EMBEDDED_CLASS_RESOLVER = ClassResolvers.getEmbeddedJarsClassResolver();
    }
}
