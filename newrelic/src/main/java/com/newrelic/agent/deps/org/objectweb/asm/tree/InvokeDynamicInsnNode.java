// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Handle;

public class InvokeDynamicInsnNode extends AbstractInsnNode
{
    public String name;
    public String desc;
    public Handle bsm;
    public Object[] bsmArgs;
    
    public InvokeDynamicInsnNode(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        super(186);
        this.name = name;
        this.desc = desc;
        this.bsm = bsm;
        this.bsmArgs = bsmArgs;
    }
    
    public int getType() {
        return 6;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
        this.acceptAnnotations(methodVisitor);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs).cloneAnnotations(this);
    }
}
