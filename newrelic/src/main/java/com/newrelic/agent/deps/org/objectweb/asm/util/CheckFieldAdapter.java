// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.util;

import com.newrelic.agent.deps.org.objectweb.asm.Attribute;
import com.newrelic.agent.deps.org.objectweb.asm.TypePath;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;

public class CheckFieldAdapter extends FieldVisitor
{
    private boolean end;
    static /* synthetic */ Class class$org$objectweb$asm$util$CheckFieldAdapter;
    
    public CheckFieldAdapter(final FieldVisitor fieldVisitor) {
        this(327680, fieldVisitor);
        if (this.getClass() != CheckFieldAdapter.class$org$objectweb$asm$util$CheckFieldAdapter) {
            throw new IllegalStateException();
        }
    }
    
    protected CheckFieldAdapter(final int n, final FieldVisitor fieldVisitor) {
        super(n, fieldVisitor);
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        this.checkEnd();
        CheckMethodAdapter.checkDesc(s, false);
        return new CheckAnnotationAdapter(super.visitAnnotation(s, b));
    }
    
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        this.checkEnd();
        final int n2 = n >>> 24;
        if (n2 != 19) {
            throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(n2));
        }
        CheckClassAdapter.checkTypeRefAndPath(n, typePath);
        CheckMethodAdapter.checkDesc(s, false);
        return new CheckAnnotationAdapter(super.visitTypeAnnotation(n, typePath, s, b));
    }
    
    public void visitAttribute(final Attribute attribute) {
        this.checkEnd();
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid attribute (must not be null)");
        }
        super.visitAttribute(attribute);
    }
    
    public void visitEnd() {
        this.checkEnd();
        this.end = true;
        super.visitEnd();
    }
    
    private void checkEnd() {
        if (this.end) {
            throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
        }
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        CheckFieldAdapter.class$org$objectweb$asm$util$CheckFieldAdapter = class$("com.newrelic.agent.deps.org.objectweb.asm.util.CheckFieldAdapter");
    }
}
