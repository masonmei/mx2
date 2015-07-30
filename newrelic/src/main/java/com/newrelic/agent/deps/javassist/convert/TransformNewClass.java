// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.convert;

import com.newrelic.agent.deps.javassist.CannotCompileException;
import com.newrelic.agent.deps.javassist.bytecode.CodeIterator;
import com.newrelic.agent.deps.javassist.CtClass;
import com.newrelic.agent.deps.javassist.bytecode.CodeAttribute;
import com.newrelic.agent.deps.javassist.bytecode.ConstPool;

public final class TransformNewClass extends Transformer
{
    private int nested;
    private String classname;
    private String newClassName;
    private int newClassIndex;
    private int newMethodNTIndex;
    private int newMethodIndex;
    
    public TransformNewClass(final Transformer next, final String classname, final String newClassName) {
        super(next);
        this.classname = classname;
        this.newClassName = newClassName;
    }
    
    @Override
    public void initialize(final ConstPool cp, final CodeAttribute attr) {
        this.nested = 0;
        final boolean newClassIndex = false;
        this.newMethodIndex = (newClassIndex ? 1 : 0);
        this.newMethodNTIndex = (newClassIndex ? 1 : 0);
        this.newClassIndex = (newClassIndex ? 1 : 0);
    }
    
    @Override
    public int transform(final CtClass clazz, final int pos, final CodeIterator iterator, final ConstPool cp) throws CannotCompileException {
        final int c = iterator.byteAt(pos);
        if (c == 187) {
            final int index = iterator.u16bitAt(pos + 1);
            if (cp.getClassInfo(index).equals(this.classname)) {
                if (iterator.byteAt(pos + 3) != 89) {
                    throw new CannotCompileException("NEW followed by no DUP was found");
                }
                if (this.newClassIndex == 0) {
                    this.newClassIndex = cp.addClassInfo(this.newClassName);
                }
                iterator.write16bit(this.newClassIndex, pos + 1);
                ++this.nested;
            }
        }
        else if (c == 183) {
            final int index = iterator.u16bitAt(pos + 1);
            final int typedesc = cp.isConstructor(this.classname, index);
            if (typedesc != 0 && this.nested > 0) {
                final int nt = cp.getMethodrefNameAndType(index);
                if (this.newMethodNTIndex != nt) {
                    this.newMethodNTIndex = nt;
                    this.newMethodIndex = cp.addMethodrefInfo(this.newClassIndex, nt);
                }
                iterator.write16bit(this.newMethodIndex, pos + 1);
                --this.nested;
            }
        }
        return pos;
    }
}
