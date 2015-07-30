// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import com.newrelic.agent.deps.org.objectweb.asm.tree.TryCatchBlockNode;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;

public class TryCatchBlockSorter extends MethodNode
{
    public TryCatchBlockSorter(final MethodVisitor methodVisitor, final int n, final String s, final String s2, final String s3, final String[] array) {
        this(327680, methodVisitor, n, s, s2, s3, array);
    }
    
    protected TryCatchBlockSorter(final int n, final MethodVisitor mv, final int n2, final String s, final String s2, final String s3, final String[] array) {
        super(n, n2, s, s2, s3, array);
        this.mv = mv;
    }
    
    public void visitEnd() {
        Collections.sort((List<Object>)this.tryCatchBlocks, new TryCatchBlockSorter$1(this));
        for (int i = 0; i < this.tryCatchBlocks.size(); ++i) {
            ((TryCatchBlockNode)this.tryCatchBlocks.get(i)).updateIndex(i);
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
}
