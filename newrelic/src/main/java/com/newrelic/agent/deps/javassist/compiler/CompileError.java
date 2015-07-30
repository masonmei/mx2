// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler;

import com.newrelic.agent.deps.javassist.NotFoundException;
import com.newrelic.agent.deps.javassist.CannotCompileException;

public class CompileError extends Exception
{
    private Lex lex;
    private String reason;
    
    public CompileError(final String s, final Lex l) {
        this.reason = s;
        this.lex = l;
    }
    
    public CompileError(final String s) {
        this.reason = s;
        this.lex = null;
    }
    
    public CompileError(final CannotCompileException e) {
        this(e.getReason());
    }
    
    public CompileError(final NotFoundException e) {
        this("cannot find " + e.getMessage());
    }
    
    public Lex getLex() {
        return this.lex;
    }
    
    @Override
    public String getMessage() {
        return this.reason;
    }
    
    @Override
    public String toString() {
        return "compile error: " + this.reason;
    }
}
