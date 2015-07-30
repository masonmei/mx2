// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.util.Map;

public class Method
{
    private final String name;
    private final String desc;
    private static final Map DESCRIPTORS;
    
    public Method(final String name, final String desc) {
        this.name = name;
        this.desc = desc;
    }
    
    public Method(final String s, final Type type, final Type[] array) {
        this(s, Type.getMethodDescriptor(type, array));
    }
    
    public static Method getMethod(final java.lang.reflect.Method method) {
        return new Method(method.getName(), Type.getMethodDescriptor(method));
    }
    
    public static Method getMethod(final Constructor constructor) {
        return new Method("<init>", Type.getConstructorDescriptor(constructor));
    }
    
    public static Method getMethod(final String s) throws IllegalArgumentException {
        return getMethod(s, false);
    }
    
    public static Method getMethod(final String s, final boolean b) throws IllegalArgumentException {
        final int index = s.indexOf(32);
        int n = s.indexOf(40, index) + 1;
        final int index2 = s.indexOf(41, n);
        if (index == -1 || n == -1 || index2 == -1) {
            throw new IllegalArgumentException();
        }
        final String substring = s.substring(0, index);
        final String trim = s.substring(index + 1, n - 1).trim();
        final StringBuffer sb = new StringBuffer();
        sb.append('(');
        int i;
        do {
            i = s.indexOf(44, n);
            String s2;
            if (i == -1) {
                s2 = map(s.substring(n, index2).trim(), b);
            }
            else {
                s2 = map(s.substring(n, i).trim(), b);
                n = i + 1;
            }
            sb.append(s2);
        } while (i != -1);
        sb.append(')');
        sb.append(map(substring, b));
        return new Method(trim, sb.toString());
    }
    
    private static String map(final String s, final boolean b) {
        if ("".equals(s)) {
            return s;
        }
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        while ((n = s.indexOf("[]", n) + 1) > 0) {
            sb.append('[');
        }
        final String substring = s.substring(0, s.length() - sb.length() * 2);
        final String s2 = Method.DESCRIPTORS.get(substring);
        if (s2 != null) {
            sb.append(s2);
        }
        else {
            sb.append('L');
            if (substring.indexOf(46) < 0) {
                if (!b) {
                    sb.append("java/lang/");
                }
                sb.append(substring);
            }
            else {
                sb.append(substring.replace('.', '/'));
            }
            sb.append(';');
        }
        return sb.toString();
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescriptor() {
        return this.desc;
    }
    
    public Type getReturnType() {
        return Type.getReturnType(this.desc);
    }
    
    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.desc);
    }
    
    public String toString() {
        return this.name + this.desc;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof Method)) {
            return false;
        }
        final Method method = (Method)o;
        return this.name.equals(method.name) && this.desc.equals(method.desc);
    }
    
    public int hashCode() {
        return this.name.hashCode() ^ this.desc.hashCode();
    }
    
    static {
        _clinit_();
        (DESCRIPTORS = new HashMap()).put("void", "V");
        Method.DESCRIPTORS.put("byte", "B");
        Method.DESCRIPTORS.put("char", "C");
        Method.DESCRIPTORS.put("double", "D");
        Method.DESCRIPTORS.put("float", "F");
        Method.DESCRIPTORS.put("int", "I");
        Method.DESCRIPTORS.put("long", "J");
        Method.DESCRIPTORS.put("short", "S");
        Method.DESCRIPTORS.put("boolean", "Z");
    }
    
    static /* synthetic */ void _clinit_() {
    }
}
