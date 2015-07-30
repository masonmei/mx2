// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.convert;

import com.newrelic.agent.deps.javassist.bytecode.BadBytecode;
import com.newrelic.agent.deps.javassist.bytecode.CodeIterator;
import com.newrelic.agent.deps.javassist.CannotCompileException;
import com.newrelic.agent.deps.javassist.bytecode.MethodInfo;
import com.newrelic.agent.deps.javassist.CtClass;
import com.newrelic.agent.deps.javassist.bytecode.CodeAttribute;
import com.newrelic.agent.deps.javassist.bytecode.ConstPool;
import com.newrelic.agent.deps.javassist.bytecode.Opcode;

public abstract class Transformer implements Opcode
{
    private Transformer next;
    
    public Transformer(final Transformer t) {
        this.next = t;
    }
    
    public Transformer getNext() {
        return this.next;
    }
    
    public void initialize(final ConstPool cp, final CodeAttribute attr) {
    }
    
    public void initialize(final ConstPool cp, final CtClass clazz, final MethodInfo minfo) throws CannotCompileException {
        this.initialize(cp, minfo.getCodeAttribute());
    }
    
    public void clean() {
    }
    
    public abstract int transform(final CtClass p0, final int p1, final CodeIterator p2, final ConstPool p3) throws CannotCompileException, BadBytecode;
    
    public int extraLocals() {
        return 0;
    }
    
    public int extraStack() {
        return 0;
    }
}
