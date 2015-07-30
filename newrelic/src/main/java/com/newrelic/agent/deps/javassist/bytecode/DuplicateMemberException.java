// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.bytecode;

import com.newrelic.agent.deps.javassist.CannotCompileException;

public class DuplicateMemberException extends CannotCompileException
{
    public DuplicateMemberException(final String msg) {
        super(msg);
    }
}
