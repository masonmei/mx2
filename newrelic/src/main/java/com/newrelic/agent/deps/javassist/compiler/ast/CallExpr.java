// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler.ast;

import com.newrelic.agent.deps.javassist.compiler.CompileError;
import com.newrelic.agent.deps.javassist.compiler.MemberResolver;

public class CallExpr extends Expr
{
    private MemberResolver.Method method;
    
    private CallExpr(final ASTree _head, final ASTList _tail) {
        super(67, _head, _tail);
        this.method = null;
    }
    
    public void setMethod(final MemberResolver.Method m) {
        this.method = m;
    }
    
    public MemberResolver.Method getMethod() {
        return this.method;
    }
    
    public static CallExpr makeCall(final ASTree target, final ASTree args) {
        return new CallExpr(target, new ASTList(args));
    }
    
    @Override
    public void accept(final Visitor v) throws CompileError {
        v.atCallExpr(this);
    }
}
