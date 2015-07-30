// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.tree;

import java.util.ArrayList;

class MethodNode$1 extends ArrayList
{
    final /* synthetic */ MethodNode this$0;
    
    MethodNode$1(final MethodNode this$0, final int n) {
        this.this$0 = this$0;
        super(n);
    }
    
    public boolean add(final Object annotationDefault) {
        this.this$0.annotationDefault = annotationDefault;
        return super.add(annotationDefault);
    }
}
