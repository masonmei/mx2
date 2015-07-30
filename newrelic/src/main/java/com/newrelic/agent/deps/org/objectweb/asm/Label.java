// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

public class Label
{
    public Object info;
    int a;
    int b;
    int c;
    private int d;
    private int[] e;
    int f;
    int g;
    Frame h;
    Label i;
    Edge j;
    Label k;
    
    public int getOffset() {
        if ((this.a & 0x2) == 0x0) {
            throw new IllegalStateException("Label offset position has not been resolved yet");
        }
        return this.c;
    }
    
    void a(final MethodWriter methodWriter, final ByteVector byteVector, final int n, final boolean b) {
        if ((this.a & 0x2) == 0x0) {
            if (b) {
                this.a(-1 - n, byteVector.b);
                byteVector.putInt(-1);
            }
            else {
                this.a(n, byteVector.b);
                byteVector.putShort(-1);
            }
        }
        else if (b) {
            byteVector.putInt(this.c - n);
        }
        else {
            byteVector.putShort(this.c - n);
        }
    }
    
    private void a(final int n, final int n2) {
        if (this.e == null) {
            this.e = new int[6];
        }
        if (this.d >= this.e.length) {
            final int[] e = new int[this.e.length + 6];
            System.arraycopy(this.e, 0, e, 0, this.e.length);
            this.e = e;
        }
        this.e[this.d++] = n;
        this.e[this.d++] = n2;
    }
    
    boolean a(final MethodWriter methodWriter, final int c, final byte[] array) {
        boolean b = false;
        this.a |= 0x2;
        this.c = c;
        int i = 0;
        while (i < this.d) {
            final int n = this.e[i++];
            int n2 = this.e[i++];
            if (n >= 0) {
                final int n3 = c - n;
                if (n3 < -32768 || n3 > 32767) {
                    final int n4 = array[n2 - 1] & 0xFF;
                    if (n4 <= 168) {
                        array[n2 - 1] = (byte)(n4 + 49);
                    }
                    else {
                        array[n2 - 1] = (byte)(n4 + 20);
                    }
                    b = true;
                }
                array[n2++] = (byte)(n3 >>> 8);
                array[n2] = (byte)n3;
            }
            else {
                final int n5 = c + n + 1;
                array[n2++] = (byte)(n5 >>> 24);
                array[n2++] = (byte)(n5 >>> 16);
                array[n2++] = (byte)(n5 >>> 8);
                array[n2] = (byte)n5;
            }
        }
        return b;
    }
    
    Label a() {
        return (this.h == null) ? this : this.h.b;
    }
    
    boolean a(final long n) {
        return (this.a & 0x400) != 0x0 && (this.e[(int)(n >>> 32)] & (int)n) != 0x0;
    }
    
    boolean a(final Label label) {
        if ((this.a & 0x400) == 0x0 || (label.a & 0x400) == 0x0) {
            return false;
        }
        for (int i = 0; i < this.e.length; ++i) {
            if ((this.e[i] & label.e[i]) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    void a(final long n, final int n2) {
        if ((this.a & 0x400) == 0x0) {
            this.a |= 0x400;
            this.e = new int[n2 / 32 + 1];
        }
        final int[] e = this.e;
        final int n3 = (int)(n >>> 32);
        e[n3] |= (int)n;
    }
    
    void b(final Label label, final long n, final int n2) {
        Label k = this;
        while (k != null) {
            final Label label2 = k;
            k = label2.k;
            label2.k = null;
            if (label != null) {
                if ((label2.a & 0x800) != 0x0) {
                    continue;
                }
                final Label label3 = label2;
                label3.a |= 0x800;
                if ((label2.a & 0x100) != 0x0 && !label2.a(label)) {
                    final Edge j = new Edge();
                    j.a = label2.f;
                    j.b = label.j.b;
                    j.c = label2.j;
                    label2.j = j;
                }
            }
            else {
                if (label2.a(n)) {
                    continue;
                }
                label2.a(n, n2);
            }
            for (Edge edge = label2.j; edge != null; edge = edge.c) {
                if (((label2.a & 0x80) == 0x0 || edge != label2.j.c) && edge.b.k == null) {
                    edge.b.k = k;
                    k = edge.b;
                }
            }
        }
    }
    
    public String toString() {
        return "L" + System.identityHashCode(this);
    }
}
