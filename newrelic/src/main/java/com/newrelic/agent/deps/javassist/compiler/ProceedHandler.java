// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler;

import com.newrelic.agent.deps.javassist.compiler.ast.ASTList;
import com.newrelic.agent.deps.javassist.bytecode.Bytecode;

public interface ProceedHandler
{
    void doit(JvstCodeGen p0, Bytecode p1, ASTList p2) throws CompileError;
    
    void setReturnType(JvstTypeChecker p0, ASTList p1) throws CompileError;
}
