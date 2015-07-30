// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

public class Attribute
{
    public final String type;
    byte[] b;
    Attribute a;
    
    protected Attribute(final String type) {
        this.type = type;
    }
    
    public boolean isUnknown() {
        return true;
    }
    
    public boolean isCodeAttribute() {
        return false;
    }
    
    protected Label[] getLabels() {
        return null;
    }
    
    protected Attribute read(final ClassReader classReader, final int n, final int n2, final char[] array, final int n3, final Label[] array2) {
        final Attribute attribute = new Attribute(this.type);
        attribute.b = new byte[n2];
        System.arraycopy(classReader.b, n, attribute.b, 0, n2);
        return attribute;
    }
    
    protected ByteVector write(final ClassWriter classWriter, final byte[] array, final int n, final int n2, final int n3) {
        final ByteVector byteVector = new ByteVector();
        byteVector.a = this.b;
        byteVector.b = this.b.length;
        return byteVector;
    }
    
    final int a() {
        int n = 0;
        for (Attribute a = this; a != null; a = a.a) {
            ++n;
        }
        return n;
    }
    
    final int a(final ClassWriter classWriter, final byte[] array, final int n, final int n2, final int n3) {
        Attribute a = this;
        int n4 = 0;
        while (a != null) {
            classWriter.newUTF8(a.type);
            n4 += a.write(classWriter, array, n, n2, n3).b + 6;
            a = a.a;
        }
        return n4;
    }
    
    final void a(final ClassWriter classWriter, final byte[] array, final int n, final int n2, final int n3, final ByteVector byteVector) {
        for (Attribute a = this; a != null; a = a.a) {
            final ByteVector write = a.write(classWriter, array, n, n2, n3);
            byteVector.putShort(classWriter.newUTF8(a.type)).putInt(write.b);
            byteVector.putByteArray(write.a, 0, write.b);
        }
    }
}
