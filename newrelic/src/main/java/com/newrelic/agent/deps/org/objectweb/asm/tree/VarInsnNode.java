// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class VarInsnNode extends AbstractInsnNode
{
    public int var;
    
    public VarInsnNode(final int n, final int var) {
        super(n);
        this.var = var;
    }
    
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }
    
    public int getType() {
        return 2;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitVarInsn(this.opcode, this.var);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new VarInsnNode(this.opcode, this.var).cloneAnnotations(this);
    }
}
