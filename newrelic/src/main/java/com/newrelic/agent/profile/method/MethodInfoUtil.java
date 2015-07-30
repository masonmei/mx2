// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile.method;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.lang.reflect.Member;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;
import com.newrelic.agent.profile.MethodLineNumberMatcher;

public class MethodInfoUtil
{
    public static MethodInfo createMethodInfo(final Class<?> declaringClass, final String methodName, final int lineNumber) {
        final String methodDesc = MethodLineNumberMatcher.getMethodDescription(declaringClass, methodName, lineNumber);
        return getMethodInfo(declaringClass, methodName, methodDesc);
    }
    
    protected static MethodInfo getMethodInfo(final Class<?> declaringClass, final String methodName, final String methodDesc) {
        if (methodDesc == null) {
            return handleNoMethodDesc(declaringClass, methodName);
        }
        final List<String> args = getArguments(methodDesc);
        if (isConstructor(methodName)) {
            return handleConstructor(declaringClass, methodName, methodDesc, args);
        }
        return handleMethod(declaringClass, methodName, methodDesc, args);
    }
    
    private static MethodInfo handleMethod(final Class<?> declaringClass, final String methodName, final String methodDesc, final List<String> args) {
        final List<Member> members = (List<Member>)Lists.newArrayList();
        if (getMethod(members, declaringClass, methodName, args)) {
            return new ExactMethodInfo(args, members.get(0));
        }
        return new MultipleMethodInfo((Set<Member>)Sets.newHashSet((Iterable<?>)members));
    }
    
    private static MethodInfo handleConstructor(final Class<?> declaringClass, final String methodName, final String methodDesc, final List<String> args) {
        final List<Member> members = (List<Member>)Lists.newArrayList();
        if (getConstructor(members, declaringClass, methodName, args)) {
            return new ExactMethodInfo(args, members.get(0));
        }
        return new MultipleMethodInfo((Set<Member>)Sets.newHashSet((Iterable<?>)members));
    }
    
    private static MethodInfo handleNoMethodDesc(final Class<?> declaringClass, final String methodName) {
        if (isConstructor(methodName)) {
            return new MultipleMethodInfo((Set<Member>)Sets.newHashSet(declaringClass.getDeclaredConstructors()));
        }
        return new MultipleMethodInfo(getMatchingMethods(declaringClass, methodName));
    }
    
    protected static List<String> getArguments(final Member m) {
        final List<String> paramClasses = (List<String>)Lists.newArrayList();
        Class<?>[] params;
        if (m instanceof Method) {
            params = ((Method)m).getParameterTypes();
        }
        else if (m instanceof Constructor) {
            params = (Class<?>[])((Constructor)m).getParameterTypes();
        }
        else {
            params = (Class<?>[])new Class[0];
        }
        for (final Class<?> clazz : params) {
            paramClasses.add(clazz.getCanonicalName());
        }
        return paramClasses;
    }
    
    protected static List<String> getArguments(final String methodDesc) {
        final Type[] types = Type.getArgumentTypes(methodDesc);
        final List<String> args = (List<String>)Lists.newArrayListWithCapacity(types.length);
        for (final Type current : types) {
            args.add(current.getClassName());
        }
        return args;
    }
    
    private static boolean isConstructor(final String methodName) {
        return methodName.startsWith("<");
    }
    
    private static boolean getConstructor(final List<Member> addToHere, final Class<?> declaringClass, final String constName, final List<String> arguments) {
        for (final Constructor<?> constructor : declaringClass.getDeclaredConstructors()) {
            addToHere.add(constructor);
            final Class<?>[] params = constructor.getParameterTypes();
            if (params.length == arguments.size()) {
                boolean matches = true;
                for (int i = 0; i < params.length; ++i) {
                    if (!arguments.get(i).equals(params[i].getCanonicalName())) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    addToHere.clear();
                    addToHere.add(constructor);
                    return true;
                }
            }
        }
        return false;
    }
    
    protected static boolean getMethod(final List<Member> addToHere, final Class<?> declaringClass, final String methodName, final List<String> arguments) {
        for (final Method method : declaringClass.getDeclaredMethods()) {
            if (methodName.equals(method.getName())) {
                addToHere.add(method);
                final Class<?>[] params = method.getParameterTypes();
                if (params.length == arguments.size()) {
                    boolean matches = true;
                    for (int i = 0; i < params.length; ++i) {
                        if (!arguments.get(i).equals(params[i].getCanonicalName())) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        addToHere.clear();
                        addToHere.add(method);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected static Set<Member> getMatchingMethods(final Class<?> declaringClass, final String methodName) {
        final Set<Member> methods = (Set<Member>)Sets.newHashSet();
        for (final Method method : declaringClass.getDeclaredMethods()) {
            if (methodName.equals(method.getName())) {
                methods.add(method);
            }
        }
        return methods;
    }
}
