// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;

final class FixLoadClassMethodAdapter extends GeneratorAdapter
{
    FixLoadClassMethodAdapter(final int access, final Method method, final MethodVisitor mv) {
        super(327680, mv, access, method.getName(), method.getDescriptor());
    }
    
    public void visitLdcInsn(final Object cst) {
        if (cst instanceof Type) {
            final Type type = (Type)cst;
            super.visitLdcInsn(type.getClassName());
            this.invokeStatic(Type.getType(Class.class), new Method("forName", Type.getType(Class.class), new Type[] { Type.getType(String.class) }));
        }
        else {
            super.visitLdcInsn(cst);
        }
    }
}
