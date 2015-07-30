// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class StaticInitMerger extends ClassVisitor
{
    private String name;
    private MethodVisitor clinit;
    private final String prefix;
    private int counter;
    
    public StaticInitMerger(final String s, final ClassVisitor classVisitor) {
        this(327680, s, classVisitor);
    }
    
    protected StaticInitMerger(final int n, final String prefix, final ClassVisitor classVisitor) {
        super(n, classVisitor);
        this.prefix = prefix;
    }
    
    public void visit(final int n, final int n2, final String name, final String s, final String s2, final String[] array) {
        this.cv.visit(n, n2, name, s, s2, array);
        this.name = name;
    }
    
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        MethodVisitor methodVisitor;
        if ("<clinit>".equals(s)) {
            final int n2 = 10;
            final String string = this.prefix + this.counter++;
            methodVisitor = this.cv.visitMethod(n2, string, s2, s3, array);
            if (this.clinit == null) {
                this.clinit = this.cv.visitMethod(n2, s, s2, null, null);
            }
            this.clinit.visitMethodInsn(184, this.name, string, s2, false);
        }
        else {
            methodVisitor = this.cv.visitMethod(n, s, s2, s3, array);
        }
        return methodVisitor;
    }
    
    public void visitEnd() {
        if (this.clinit != null) {
            this.clinit.visitInsn(177);
            this.clinit.visitMaxs(0, 0);
        }
        this.cv.visitEnd();
    }
}
