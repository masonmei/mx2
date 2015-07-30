// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.util.InsertOnlyArray;

public class ClassMethodSignatures
{
    private static final ClassMethodSignatures INSTANCE;
    private final InsertOnlyArray<ClassMethodSignature> signatures;
    
    ClassMethodSignatures() {
        this(1000);
    }
    
    ClassMethodSignatures(final int capacity) {
        this.signatures = new InsertOnlyArray<ClassMethodSignature>(capacity);
    }
    
    public static ClassMethodSignatures get() {
        return ClassMethodSignatures.INSTANCE;
    }
    
    public ClassMethodSignature get(final int index) {
        return this.signatures.get(index);
    }
    
    public int add(final ClassMethodSignature signature) {
        return this.signatures.add(signature);
    }
    
    public int getIndex(final ClassMethodSignature signature) {
        return this.signatures.getIndex(signature);
    }
    
    static {
        INSTANCE = new ClassMethodSignatures();
    }
}
