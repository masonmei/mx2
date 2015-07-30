// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class LineNumberNode extends AbstractInsnNode
{
    public int line;
    public LabelNode start;
    
    public LineNumberNode(final int line, final LabelNode start) {
        super(-1);
        this.line = line;
        this.start = start;
    }
    
    public int getType() {
        return 15;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitLineNumber(this.line, this.start.getLabel());
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new LineNumberNode(this.line, AbstractInsnNode.clone(this.start, map));
    }
}
