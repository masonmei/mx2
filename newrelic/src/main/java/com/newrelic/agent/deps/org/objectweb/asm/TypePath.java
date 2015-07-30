// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm;

public class TypePath
{
    public static final int ARRAY_ELEMENT = 0;
    public static final int INNER_TYPE = 1;
    public static final int WILDCARD_BOUND = 2;
    public static final int TYPE_ARGUMENT = 3;
    byte[] a;
    int b;
    
    TypePath(final byte[] a, final int b) {
        this.a = a;
        this.b = b;
    }
    
    public int getLength() {
        return this.a[this.b];
    }
    
    public int getStep(final int n) {
        return this.a[this.b + 2 * n + 1];
    }
    
    public int getStepArgument(final int n) {
        return this.a[this.b + 2 * n + 2];
    }
    
    public static TypePath fromString(final String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        final int length = s.length();
        final ByteVector byteVector = new ByteVector(length);
        byteVector.putByte(0);
        int i = 0;
        while (i < length) {
            final char char1 = s.charAt(i++);
            if (char1 == '[') {
                byteVector.a(0, 0);
            }
            else if (char1 == '.') {
                byteVector.a(1, 0);
            }
            else if (char1 == '*') {
                byteVector.a(2, 0);
            }
            else {
                if (char1 < '0' || char1 > '9') {
                    continue;
                }
                int n = char1 - '0';
                char char2;
                while (i < length && (char2 = s.charAt(i)) >= '0' && char2 <= '9') {
                    n = n * 10 + char2 - 48;
                    ++i;
                }
                byteVector.a(3, n);
            }
        }
        byteVector.a[0] = (byte)(byteVector.b / 2);
        return new TypePath(byteVector.a, 0);
    }
    
    public String toString() {
        final int length = this.getLength();
        final StringBuffer sb = new StringBuffer(length * 2);
        for (int i = 0; i < length; ++i) {
            switch (this.getStep(i)) {
                case 0: {
                    sb.append('[');
                    break;
                }
                case 1: {
                    sb.append('.');
                    break;
                }
                case 2: {
                    sb.append('*');
                    break;
                }
                case 3: {
                    sb.append(this.getStepArgument(i));
                    break;
                }
                default: {
                    sb.append('_');
                    break;
                }
            }
        }
        return sb.toString();
    }
}
