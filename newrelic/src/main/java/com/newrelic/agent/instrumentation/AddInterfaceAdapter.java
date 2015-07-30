// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class AddInterfaceAdapter extends ClassVisitor
{
    private final String className;
    private final Class<?> type;
    
    public AddInterfaceAdapter(final ClassVisitor cv, final String className, final Class<?> type) {
        super(327680, cv);
        this.className = className;
        this.type = type;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, this.addInterface(interfaces));
    }
    
    public void visitEnd() {
        if (Agent.LOG.isFinerEnabled()) {
            final String msg = MessageFormat.format("Appended {0} to {1}", this.type.getName(), this.className.replace('/', '.'));
            Agent.LOG.finer(msg);
        }
        super.visitEnd();
    }
    
    private String[] addInterface(final String[] interfaces) {
        final Set<String> list = new HashSet<String>(Arrays.asList(interfaces));
        list.add(Type.getType(this.type).getInternalName());
        return list.toArray(new String[list.size()]);
    }
}
