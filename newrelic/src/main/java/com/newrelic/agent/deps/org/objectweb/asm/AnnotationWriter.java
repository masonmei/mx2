// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

final class AnnotationWriter extends AnnotationVisitor
{
    private final ClassWriter a;
    private int b;
    private final boolean c;
    private final ByteVector d;
    private final ByteVector e;
    private final int f;
    AnnotationWriter g;
    AnnotationWriter h;
    
    AnnotationWriter(final ClassWriter a, final boolean c, final ByteVector d, final ByteVector e, final int f) {
        super(327680);
        this.a = a;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }
    
    public void visit(final String s, final Object o) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(s));
        }
        if (o instanceof String) {
            this.d.b(115, this.a.newUTF8((String)o));
        }
        else if (o instanceof Byte) {
            this.d.b(66, this.a.a((byte)o).a);
        }
        else if (o instanceof Boolean) {
            this.d.b(90, this.a.a(((boolean)o) ? 1 : 0).a);
        }
        else if (o instanceof Character) {
            this.d.b(67, this.a.a((char)o).a);
        }
        else if (o instanceof Short) {
            this.d.b(83, this.a.a((short)o).a);
        }
        else if (o instanceof Type) {
            this.d.b(99, this.a.newUTF8(((Type)o).getDescriptor()));
        }
        else if (o instanceof byte[]) {
            final byte[] array = (byte[])o;
            this.d.b(91, array.length);
            for (int i = 0; i < array.length; ++i) {
                this.d.b(66, this.a.a(array[i]).a);
            }
        }
        else if (o instanceof boolean[]) {
            final boolean[] array2 = (boolean[])o;
            this.d.b(91, array2.length);
            for (int j = 0; j < array2.length; ++j) {
                this.d.b(90, this.a.a(array2[j] ? 1 : 0).a);
            }
        }
        else if (o instanceof short[]) {
            final short[] array3 = (short[])o;
            this.d.b(91, array3.length);
            for (int k = 0; k < array3.length; ++k) {
                this.d.b(83, this.a.a(array3[k]).a);
            }
        }
        else if (o instanceof char[]) {
            final char[] array4 = (char[])o;
            this.d.b(91, array4.length);
            for (int l = 0; l < array4.length; ++l) {
                this.d.b(67, this.a.a(array4[l]).a);
            }
        }
        else if (o instanceof int[]) {
            final int[] array5 = (int[])o;
            this.d.b(91, array5.length);
            for (int n = 0; n < array5.length; ++n) {
                this.d.b(73, this.a.a(array5[n]).a);
            }
        }
        else if (o instanceof long[]) {
            final long[] array6 = (long[])o;
            this.d.b(91, array6.length);
            for (int n2 = 0; n2 < array6.length; ++n2) {
                this.d.b(74, this.a.a(array6[n2]).a);
            }
        }
        else if (o instanceof float[]) {
            final float[] array7 = (float[])o;
            this.d.b(91, array7.length);
            for (int n3 = 0; n3 < array7.length; ++n3) {
                this.d.b(70, this.a.a(array7[n3]).a);
            }
        }
        else if (o instanceof double[]) {
            final double[] array8 = (double[])o;
            this.d.b(91, array8.length);
            for (int n4 = 0; n4 < array8.length; ++n4) {
                this.d.b(68, this.a.a(array8[n4]).a);
            }
        }
        else {
            final Item a = this.a.a(o);
            this.d.b(".s.IFJDCS".charAt(a.b), a.a);
        }
    }
    
    public void visitEnum(final String s, final String s2, final String s3) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(s));
        }
        this.d.b(101, this.a.newUTF8(s2)).putShort(this.a.newUTF8(s3));
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final String s2) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(s));
        }
        this.d.b(64, this.a.newUTF8(s2)).putShort(0);
        return new AnnotationWriter(this.a, true, this.d, this.d, this.d.b - 2);
    }
    
    public AnnotationVisitor visitArray(final String s) {
        ++this.b;
        if (this.c) {
            this.d.putShort(this.a.newUTF8(s));
        }
        this.d.b(91, 0);
        return new AnnotationWriter(this.a, false, this.d, this.d, this.d.b - 2);
    }
    
    public void visitEnd() {
        if (this.e != null) {
            final byte[] a = this.e.a;
            a[this.f] = (byte)(this.b >>> 8);
            a[this.f + 1] = (byte)this.b;
        }
    }
    
    int a() {
        int n = 0;
        for (AnnotationWriter g = this; g != null; g = g.g) {
            n += g.d.b;
        }
        return n;
    }
    
    void a(final ByteVector byteVector) {
        int n = 0;
        int n2 = 2;
        AnnotationWriter g = this;
        AnnotationWriter h = null;
        while (g != null) {
            ++n;
            n2 += g.d.b;
            g.visitEnd();
            g.h = h;
            h = g;
            g = g.g;
        }
        byteVector.putInt(n2);
        byteVector.putShort(n);
        for (AnnotationWriter h2 = h; h2 != null; h2 = h2.h) {
            byteVector.putByteArray(h2.d.a, 0, h2.d.b);
        }
    }
    
    static void a(final AnnotationWriter[] array, final int n, final ByteVector byteVector) {
        int n2 = 1 + 2 * (array.length - n);
        for (int i = n; i < array.length; ++i) {
            n2 += ((array[i] == null) ? 0 : array[i].a());
        }
        byteVector.putInt(n2).putByte(array.length - n);
        for (int j = n; j < array.length; ++j) {
            AnnotationWriter g = array[j];
            AnnotationWriter h = null;
            int n3 = 0;
            while (g != null) {
                ++n3;
                g.visitEnd();
                g.h = h;
                h = g;
                g = g.g;
            }
            byteVector.putShort(n3);
            for (AnnotationWriter h2 = h; h2 != null; h2 = h2.h) {
                byteVector.putByteArray(h2.d.a, 0, h2.d.b);
            }
        }
    }
    
    static void a(final int n, final TypePath typePath, final ByteVector byteVector) {
        switch (n >>> 24) {
            case 0:
            case 1:
            case 22: {
                byteVector.putShort(n >>> 16);
                break;
            }
            case 19:
            case 20:
            case 21: {
                byteVector.putByte(n >>> 24);
                break;
            }
            case 71:
            case 72:
            case 73:
            case 74:
            case 75: {
                byteVector.putInt(n);
                break;
            }
            default: {
                byteVector.b(n >>> 24, (n & 0xFFFF00) >> 8);
                break;
            }
        }
        if (typePath == null) {
            byteVector.putByte(0);
        }
        else {
            byteVector.putByteArray(typePath.a, typePath.b, typePath.a[typePath.b] * 2 + 1);
        }
    }
}
