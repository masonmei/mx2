// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler.ast;

import com.newrelic.agent.deps.javassist.compiler.CompileError;

public class Symbol extends ASTree
{
    protected String identifier;
    
    public Symbol(final String sym) {
        this.identifier = sym;
    }
    
    public String get() {
        return this.identifier;
    }
    
    @Override
    public String toString() {
        return this.identifier;
    }
    
    @Override
    public void accept(final Visitor v) throws CompileError {
        v.atSymbol(this);
    }
}
