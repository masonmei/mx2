// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

final class FieldWriter extends FieldVisitor
{
    private final ClassWriter b;
    private final int c;
    private final int d;
    private final int e;
    private int f;
    private int g;
    private AnnotationWriter h;
    private AnnotationWriter i;
    private AnnotationWriter k;
    private AnnotationWriter l;
    private Attribute j;
    
    FieldWriter(final ClassWriter b, final int c, final String s, final String s2, final String s3, final Object o) {
        super(327680);
        if (b.B == null) {
            b.B = this;
        }
        else {
            b.C.fv = this;
        }
        b.C = this;
        this.b = b;
        this.c = c;
        this.d = b.newUTF8(s);
        this.e = b.newUTF8(s2);
        if (s3 != null) {
            this.f = b.newUTF8(s3);
        }
        if (o != null) {
            this.g = b.a(o).a;
        }
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final ByteVector byteVector = new ByteVector();
        byteVector.putShort(this.b.newUTF8(s)).putShort(0);
        final AnnotationWriter annotationWriter = new AnnotationWriter(this.b, true, byteVector, byteVector, 2);
        if (b) {
            annotationWriter.g = this.h;
            this.h = annotationWriter;
        }
        else {
            annotationWriter.g = this.i;
            this.i = annotationWriter;
        }
        return annotationWriter;
    }
    
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        final ByteVector byteVector = new ByteVector();
        AnnotationWriter.a(n, typePath, byteVector);
        byteVector.putShort(this.b.newUTF8(s)).putShort(0);
        final AnnotationWriter annotationWriter = new AnnotationWriter(this.b, true, byteVector, byteVector, byteVector.b - 2);
        if (b) {
            annotationWriter.g = this.k;
            this.k = annotationWriter;
        }
        else {
            annotationWriter.g = this.l;
            this.l = annotationWriter;
        }
        return annotationWriter;
    }
    
    public void visitAttribute(final Attribute j) {
        j.a = this.j;
        this.j = j;
    }
    
    public void visitEnd() {
    }
    
    int a() {
        int n = 8;
        if (this.g != 0) {
            this.b.newUTF8("ConstantValue");
            n += 8;
        }
        if ((this.c & 0x1000) != 0x0 && ((this.b.b & 0xFFFF) < 49 || (this.c & 0x40000) != 0x0)) {
            this.b.newUTF8("Synthetic");
            n += 6;
        }
        if ((this.c & 0x20000) != 0x0) {
            this.b.newUTF8("Deprecated");
            n += 6;
        }
        if (this.f != 0) {
            this.b.newUTF8("Signature");
            n += 8;
        }
        if (this.h != null) {
            this.b.newUTF8("RuntimeVisibleAnnotations");
            n += 8 + this.h.a();
        }
        if (this.i != null) {
            this.b.newUTF8("RuntimeInvisibleAnnotations");
            n += 8 + this.i.a();
        }
        if (this.k != null) {
            this.b.newUTF8("RuntimeVisibleTypeAnnotations");
            n += 8 + this.k.a();
        }
        if (this.l != null) {
            this.b.newUTF8("RuntimeInvisibleTypeAnnotations");
            n += 8 + this.l.a();
        }
        if (this.j != null) {
            n += this.j.a(this.b, null, 0, -1, -1);
        }
        return n;
    }
    
    void a(final ByteVector byteVector) {
        byteVector.putShort(this.c & ~(0x60000 | (this.c & 0x40000) / 64)).putShort(this.d).putShort(this.e);
        int n = 0;
        if (this.g != 0) {
            ++n;
        }
        if ((this.c & 0x1000) != 0x0 && ((this.b.b & 0xFFFF) < 49 || (this.c & 0x40000) != 0x0)) {
            ++n;
        }
        if ((this.c & 0x20000) != 0x0) {
            ++n;
        }
        if (this.f != 0) {
            ++n;
        }
        if (this.h != null) {
            ++n;
        }
        if (this.i != null) {
            ++n;
        }
        if (this.k != null) {
            ++n;
        }
        if (this.l != null) {
            ++n;
        }
        if (this.j != null) {
            n += this.j.a();
        }
        byteVector.putShort(n);
        if (this.g != 0) {
            byteVector.putShort(this.b.newUTF8("ConstantValue"));
            byteVector.putInt(2).putShort(this.g);
        }
        if ((this.c & 0x1000) != 0x0 && ((this.b.b & 0xFFFF) < 49 || (this.c & 0x40000) != 0x0)) {
            byteVector.putShort(this.b.newUTF8("Synthetic")).putInt(0);
        }
        if ((this.c & 0x20000) != 0x0) {
            byteVector.putShort(this.b.newUTF8("Deprecated")).putInt(0);
        }
        if (this.f != 0) {
            byteVector.putShort(this.b.newUTF8("Signature"));
            byteVector.putInt(2).putShort(this.f);
        }
        if (this.h != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeVisibleAnnotations"));
            this.h.a(byteVector);
        }
        if (this.i != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeInvisibleAnnotations"));
            this.i.a(byteVector);
        }
        if (this.k != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeVisibleTypeAnnotations"));
            this.k.a(byteVector);
        }
        if (this.l != null) {
            byteVector.putShort(this.b.newUTF8("RuntimeInvisibleTypeAnnotations"));
            this.l.a(byteVector);
        }
        if (this.j != null) {
            this.j.a(this.b, null, 0, -1, -1, byteVector);
        }
    }
}
