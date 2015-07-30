// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class InsnNode extends AbstractInsnNode
{
    public InsnNode(final int n) {
        super(n);
    }
    
    public int getType() {
        return 0;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(this.opcode);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new InsnNode(this.opcode).cloneAnnotations(this);
    }
}
