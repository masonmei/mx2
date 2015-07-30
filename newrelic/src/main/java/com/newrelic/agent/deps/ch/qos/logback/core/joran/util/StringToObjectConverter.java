// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.Charset;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;

public class StringToObjectConverter
{
    private static final Class[] STING_CLASS_PARAMETER;
    
    public static boolean canBeBuiltFromSimpleString(final Class<?> parameterClass) {
        final Package p = parameterClass.getPackage();
        return parameterClass.isPrimitive() || (p != null && "java.lang".equals(p.getName())) || followsTheValueOfConvention(parameterClass) || parameterClass.isEnum() || isOfTypeCharset(parameterClass);
    }
    
    public static Object convertArg(final ContextAware ca, final String val, final Class<?> type) {
        if (val == null) {
            return null;
        }
        final String v = val.trim();
        if (String.class.isAssignableFrom(type)) {
            return v;
        }
        if (Integer.TYPE.isAssignableFrom(type)) {
            return new Integer(v);
        }
        if (Long.TYPE.isAssignableFrom(type)) {
            return new Long(v);
        }
        if (Float.TYPE.isAssignableFrom(type)) {
            return new Float(v);
        }
        if (Double.TYPE.isAssignableFrom(type)) {
            return new Double(v);
        }
        if (Boolean.TYPE.isAssignableFrom(type)) {
            if ("true".equalsIgnoreCase(v)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(v)) {
                return Boolean.FALSE;
            }
        }
        else {
            if (type.isEnum()) {
                return convertToEnum(ca, v, (Class<? extends Enum>)type);
            }
            if (followsTheValueOfConvention(type)) {
                return convertByValueOfMethod(ca, type, v);
            }
            if (isOfTypeCharset(type)) {
                return convertToCharset(ca, val);
            }
        }
        return null;
    }
    
    private static boolean isOfTypeCharset(final Class<?> type) {
        return Charset.class.isAssignableFrom(type);
    }
    
    private static Charset convertToCharset(final ContextAware ca, final String val) {
        try {
            return Charset.forName(val);
        }
        catch (UnsupportedCharsetException e) {
            ca.addError("Failed to get charset [" + val + "]", e);
            return null;
        }
    }
    
    private static boolean followsTheValueOfConvention(final Class<?> parameterClass) {
        try {
            final Method valueOfMethod = parameterClass.getMethod("valueOf", (Class<?>[])StringToObjectConverter.STING_CLASS_PARAMETER);
            final int mod = valueOfMethod.getModifiers();
            if (Modifier.isStatic(mod)) {
                return true;
            }
        }
        catch (SecurityException e) {}
        catch (NoSuchMethodException ex) {}
        return false;
    }
    
    private static Object convertByValueOfMethod(final ContextAware ca, final Class<?> type, final String val) {
        try {
            final Method valueOfMethod = type.getMethod("valueOf", (Class<?>[])StringToObjectConverter.STING_CLASS_PARAMETER);
            return valueOfMethod.invoke(null, val);
        }
        catch (Exception e) {
            ca.addError("Failed to invoke valueOf{} method in class [" + type.getName() + "] with value [" + val + "]");
            return null;
        }
    }
    
    private static Object convertToEnum(final ContextAware ca, final String val, final Class<? extends Enum> enumType) {
        return Enum.valueOf((Class<Object>)enumType, val);
    }
    
    boolean isBuildableFromSimpleString() {
        return false;
    }
    
    static {
        STING_CLASS_PARAMETER = new Class[] { String.class };
    }
}
