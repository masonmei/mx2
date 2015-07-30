// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.methodmatchers;

import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import java.util.regex.Pattern;

public class GetterSetterMethodMatcher implements MethodMatcher
{
    static final Pattern GETTER_METHOD_PATTERN;
    static final Pattern IS_METHOD_PATTERN;
    static final Pattern SETTER_METHOD_PATTERN;
    static final Pattern GETTER_DESCRIPTION_PATTERN;
    static final Pattern IS_DESCRIPTION_PATTERN;
    static final Pattern SETTER_DESCRIPTION_PATTERN;
    private static GetterSetterMethodMatcher matcher;
    
    public static GetterSetterMethodMatcher getGetterSetterMethodMatcher() {
        return GetterSetterMethodMatcher.matcher;
    }
    
    public boolean matches(final int access, final String name, final String desc, final Set<String> annotations) {
        if (GetterSetterMethodMatcher.GETTER_METHOD_PATTERN.matcher(name).matches()) {
            return GetterSetterMethodMatcher.GETTER_DESCRIPTION_PATTERN.matcher(desc).matches();
        }
        if (GetterSetterMethodMatcher.IS_METHOD_PATTERN.matcher(name).matches()) {
            return GetterSetterMethodMatcher.IS_DESCRIPTION_PATTERN.matcher(desc).matches();
        }
        return GetterSetterMethodMatcher.SETTER_METHOD_PATTERN.matcher(name).matches() && GetterSetterMethodMatcher.SETTER_DESCRIPTION_PATTERN.matcher(desc).matches();
    }
    
    public Method[] getExactMethods() {
        return null;
    }
    
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    static {
        GETTER_METHOD_PATTERN = Pattern.compile("^get[A-Z][a-zA-Z0-9_]*$");
        IS_METHOD_PATTERN = Pattern.compile("^is[A-Z][a-zA-Z0-9_]*$");
        SETTER_METHOD_PATTERN = Pattern.compile("^set[A-Z][a-zA-Z0-9_]*$");
        GETTER_DESCRIPTION_PATTERN = Pattern.compile("^\\(\\)[^V].*$");
        IS_DESCRIPTION_PATTERN = Pattern.compile("^\\(\\)(?:Z|Ljava/lang/Boolean;)$");
        SETTER_DESCRIPTION_PATTERN = Pattern.compile("^\\(\\[?[A-Z][a-zA-Z0-9_/;]*\\)V$");
        GetterSetterMethodMatcher.matcher = new GetterSetterMethodMatcher();
    }
}
