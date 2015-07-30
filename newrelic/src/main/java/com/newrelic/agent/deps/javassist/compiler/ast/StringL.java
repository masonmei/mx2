// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler.ast;

import com.newrelic.agent.deps.javassist.compiler.CompileError;

public class StringL extends ASTree
{
    protected String text;
    
    public StringL(final String t) {
        this.text = t;
    }
    
    public String get() {
        return this.text;
    }
    
    @Override
    public String toString() {
        return "\"" + this.text + "\"";
    }
    
    @Override
    public void accept(final Visitor v) throws CompileError {
        v.atStringL(this);
    }
}
