// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler.ast;

import com.newrelic.agent.deps.javassist.compiler.CompileError;

public class ArrayInit extends ASTList
{
    public ArrayInit(final ASTree firstElement) {
        super(firstElement);
    }
    
    @Override
    public void accept(final Visitor v) throws CompileError {
        v.atArrayInit(this);
    }
    
    public String getTag() {
        return "array";
    }
}
