// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class TypeInsnNode extends AbstractInsnNode
{
    public String desc;
    
    public TypeInsnNode(final int n, final String desc) {
        super(n);
        this.desc = desc;
    }
    
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }
    
    public int getType() {
        return 3;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitTypeInsn(this.opcode, this.desc);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new TypeInsnNode(this.opcode, this.desc).cloneAnnotations(this);
    }
}
