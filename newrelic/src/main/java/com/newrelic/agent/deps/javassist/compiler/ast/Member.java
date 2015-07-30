// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler.ast;

import com.newrelic.agent.deps.javassist.compiler.CompileError;
import com.newrelic.agent.deps.javassist.CtField;

public class Member extends Symbol
{
    private CtField field;
    
    public Member(final String name) {
        super(name);
        this.field = null;
    }
    
    public void setField(final CtField f) {
        this.field = f;
    }
    
    public CtField getField() {
        return this.field;
    }
    
    @Override
    public void accept(final Visitor v) throws CompileError {
        v.atMember(this);
    }
}
