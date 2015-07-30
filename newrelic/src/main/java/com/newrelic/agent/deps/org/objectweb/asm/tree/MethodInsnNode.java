// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class MethodInsnNode extends AbstractInsnNode
{
    public String owner;
    public String name;
    public String desc;
    public boolean itf;
    
    public MethodInsnNode(final int n, final String s, final String s2, final String s3) {
        this(n, s, s2, s3, n == 185);
    }
    
    public MethodInsnNode(final int n, final String owner, final String name, final String desc, final boolean itf) {
        super(n);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.itf = itf;
    }
    
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }
    
    public int getType() {
        return 5;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitMethodInsn(this.opcode, this.owner, this.name, this.desc, this.itf);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new MethodInsnNode(this.opcode, this.owner, this.name, this.desc, this.itf);
    }
}
