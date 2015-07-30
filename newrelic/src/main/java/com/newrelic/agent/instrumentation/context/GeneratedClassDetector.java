// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.regex.Pattern;

public class GeneratedClassDetector implements ClassMatchVisitorFactory
{
    static final boolean isGenerated(final String className) {
        final Pattern proxy = Pattern.compile("(.*)proxy(.*)", 2);
        final Pattern cglib = Pattern.compile("(.*)cglib(.*)", 2);
        final Pattern generated = Pattern.compile("(.*)generated(.*)", 2);
        return className != null && (className.contains("$$") || generated.matcher(className).find() || cglib.matcher(className).find() || proxy.matcher(className).find());
    }
    
    public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
        return new ClassVisitor(327680, cv) {
            public void visitSource(final String source, final String debug) {
                super.visitSource(source, debug);
                context.setSourceAttribute(true);
                if ("<generated>".equals(source)) {
                    context.setGenerated(true);
                }
            }
            
            public void visitEnd() {
                super.visitEnd();
                if (!context.hasSourceAttribute()) {
                    final String className = reader.getClassName();
                    if (GeneratedClassDetector.isGenerated(className)) {
                        context.setGenerated(true);
                    }
                }
            }
        };
    }
}
