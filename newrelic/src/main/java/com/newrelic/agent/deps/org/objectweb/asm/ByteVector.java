// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

public class ByteVector
{
    byte[] a;
    int b;
    
    public ByteVector() {
        this.a = new byte[64];
    }
    
    public ByteVector(final int n) {
        this.a = new byte[n];
    }
    
    public ByteVector putByte(final int n) {
        int b = this.b;
        if (b + 1 > this.a.length) {
            this.a(1);
        }
        this.a[b++] = (byte)n;
        this.b = b;
        return this;
    }
    
    ByteVector a(final int n, final int n2) {
        int b = this.b;
        if (b + 2 > this.a.length) {
            this.a(2);
        }
        final byte[] a = this.a;
        a[b++] = (byte)n;
        a[b++] = (byte)n2;
        this.b = b;
        return this;
    }
    
    public ByteVector putShort(final int n) {
        int b = this.b;
        if (b + 2 > this.a.length) {
            this.a(2);
        }
        final byte[] a = this.a;
        a[b++] = (byte)(n >>> 8);
        a[b++] = (byte)n;
        this.b = b;
        return this;
    }
    
    ByteVector b(final int n, final int n2) {
        int b = this.b;
        if (b + 3 > this.a.length) {
            this.a(3);
        }
        final byte[] a = this.a;
        a[b++] = (byte)n;
        a[b++] = (byte)(n2 >>> 8);
        a[b++] = (byte)n2;
        this.b = b;
        return this;
    }
    
    public ByteVector putInt(final int n) {
        int b = this.b;
        if (b + 4 > this.a.length) {
            this.a(4);
        }
        final byte[] a = this.a;
        a[b++] = (byte)(n >>> 24);
        a[b++] = (byte)(n >>> 16);
        a[b++] = (byte)(n >>> 8);
        a[b++] = (byte)n;
        this.b = b;
        return this;
    }
    
    public ByteVector putLong(final long n) {
        int b = this.b;
        if (b + 8 > this.a.length) {
            this.a(8);
        }
        final byte[] a = this.a;
        final int n2 = (int)(n >>> 32);
        a[b++] = (byte)(n2 >>> 24);
        a[b++] = (byte)(n2 >>> 16);
        a[b++] = (byte)(n2 >>> 8);
        a[b++] = (byte)n2;
        final int n3 = (int)n;
        a[b++] = (byte)(n3 >>> 24);
        a[b++] = (byte)(n3 >>> 16);
        a[b++] = (byte)(n3 >>> 8);
        a[b++] = (byte)n3;
        this.b = b;
        return this;
    }
    
    public ByteVector putUTF8(final String s) {
        final int length = s.length();
        if (length > 65535) {
            throw new IllegalArgumentException();
        }
        int b = this.b;
        if (b + 2 + length > this.a.length) {
            this.a(2 + length);
        }
        final byte[] a = this.a;
        a[b++] = (byte)(length >>> 8);
        a[b++] = (byte)length;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 < '\u0001' || char1 > '\u007f') {
                this.b = b;
                return this.encodeUTF8(s, i, 65535);
            }
            a[b++] = (byte)char1;
        }
        this.b = b;
        return this;
    }
    
    ByteVector encodeUTF8(final String s, final int n, final int n2) {
        final int length = s.length();
        int n3 = n;
        for (int i = n; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 >= '\u0001' && char1 <= '\u007f') {
                ++n3;
            }
            else if (char1 > '\u07ff') {
                n3 += 3;
            }
            else {
                n3 += 2;
            }
        }
        if (n3 > n2) {
            throw new IllegalArgumentException();
        }
        final int n4 = this.b - n - 2;
        if (n4 >= 0) {
            this.a[n4] = (byte)(n3 >>> 8);
            this.a[n4 + 1] = (byte)n3;
        }
        if (this.b + n3 - n > this.a.length) {
            this.a(n3 - n);
        }
        int b = this.b;
        for (int j = n; j < length; ++j) {
            final char char2 = s.charAt(j);
            if (char2 >= '\u0001' && char2 <= '\u007f') {
                this.a[b++] = (byte)char2;
            }
            else if (char2 > '\u07ff') {
                this.a[b++] = (byte)('\u00e0' | (char2 >> 12 & '\u000f'));
                this.a[b++] = (byte)('\u0080' | (char2 >> 6 & '?'));
                this.a[b++] = (byte)('\u0080' | (char2 & '?'));
            }
            else {
                this.a[b++] = (byte)('\u00c0' | (char2 >> 6 & '\u001f'));
                this.a[b++] = (byte)('\u0080' | (char2 & '?'));
            }
        }
        this.b = b;
        return this;
    }
    
    public ByteVector putByteArray(final byte[] array, final int n, final int n2) {
        if (this.b + n2 > this.a.length) {
            this.a(n2);
        }
        if (array != null) {
            System.arraycopy(array, n, this.a, this.b, n2);
        }
        this.b += n2;
        return this;
    }
    
    private void a(final int n) {
        final int n2 = 2 * this.a.length;
        final int n3 = this.b + n;
        final byte[] a = new byte[(n2 > n3) ? n2 : n3];
        System.arraycopy(this.a, 0, a, 0, this.b);
        this.a = a;
    }
}
