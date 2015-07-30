// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler;

public class SyntaxError extends CompileError
{
    public SyntaxError(final Lex lexer) {
        super("syntax error near \"" + lexer.getTextAround() + "\"", lexer);
    }
}
