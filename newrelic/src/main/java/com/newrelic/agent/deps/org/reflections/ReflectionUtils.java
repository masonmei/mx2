// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections;

import java.util.Collections;
import java.util.ArrayList;
import com.newrelic.agent.deps.org.reflections.util.ClasspathHelper;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import java.lang.reflect.Member;
import com.newrelic.agent.deps.com.google.common.collect.Iterables;
import com.newrelic.agent.deps.com.google.common.base.Predicates;
import com.newrelic.agent.deps.org.reflections.util.Utils;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.base.Predicate;
import java.util.List;

public abstract class ReflectionUtils
{
    public static boolean includeObject;
    private static List<String> primitiveNames;
    private static List<Class> primitiveTypes;
    private static List<String> primitiveDescriptors;
    
    public static Set<Class<?>> getAllSuperTypes(final Class<?> type, final Predicate<? super Class<?>>... predicates) {
        final Set<Class<?>> result = (Set<Class<?>>)Sets.newLinkedHashSet();
        if (type != null && (ReflectionUtils.includeObject || !type.equals(Object.class))) {
            result.add(type);
            result.addAll(getAllSuperTypes(type.getSuperclass(), (Predicate<? super Class<?>>[])new Predicate[0]));
            for (final Class<?> ifc : type.getInterfaces()) {
                result.addAll(getAllSuperTypes(ifc, (Predicate<? super Class<?>>[])new Predicate[0]));
            }
        }
        return filter(result, predicates);
    }
    
    public static Set<Method> getAllMethods(final Class<?> type, final Predicate<? super Method>... predicates) {
        final Set<Method> result = (Set<Method>)Sets.newHashSet();
        for (final Class<?> t : getAllSuperTypes(type, (Predicate<? super Class<?>>[])new Predicate[0])) {
            result.addAll(getMethods(t, predicates));
        }
        return result;
    }
    
    public static Set<Method> getMethods(final Class<?> t, final Predicate<? super Method>... predicates) {
        return filter(t.isInterface() ? t.getMethods() : t.getDeclaredMethods(), predicates);
    }
    
    public static Set<Constructor> getAllConstructors(final Class<?> type, final Predicate<? super Constructor>... predicates) {
        final Set<Constructor> result = (Set<Constructor>)Sets.newHashSet();
        for (final Class<?> t : getAllSuperTypes(type, (Predicate<? super Class<?>>[])new Predicate[0])) {
            result.addAll(getConstructors(t, predicates));
        }
        return result;
    }
    
    public static Set<Constructor> getConstructors(final Class<?> t, final Predicate<? super Constructor>... predicates) {
        return (Set<Constructor>)filter(t.getDeclaredConstructors(), (Predicate<? super Constructor<?>>[])predicates);
    }
    
    public static Set<Field> getAllFields(final Class<?> type, final Predicate<? super Field>... predicates) {
        final Set<Field> result = (Set<Field>)Sets.newHashSet();
        for (final Class<?> t : getAllSuperTypes(type, (Predicate<? super Class<?>>[])new Predicate[0])) {
            result.addAll(getFields(t, predicates));
        }
        return result;
    }
    
    public static Set<Field> getFields(final Class<?> type, final Predicate<? super Field>... predicates) {
        return filter(type.getDeclaredFields(), predicates);
    }
    
    public static <T extends AnnotatedElement> Set<Annotation> getAllAnnotations(final T type, final Predicate<Annotation>... predicates) {
        final Set<Annotation> result = (Set<Annotation>)Sets.newHashSet();
        if (type instanceof Class) {
            for (final Class<?> t : getAllSuperTypes((Class<?>)type, (Predicate<? super Class<?>>[])new Predicate[0])) {
                result.addAll(getAnnotations(t, predicates));
            }
        }
        else {
            result.addAll(getAnnotations((AnnotatedElement)type, predicates));
        }
        return result;
    }
    
    public static <T extends AnnotatedElement> Set<Annotation> getAnnotations(final T type, final Predicate<Annotation>... predicates) {
        return filter(type.getDeclaredAnnotations(), (Predicate<? super Annotation>[])predicates);
    }
    
    public static <T extends AnnotatedElement> Set<T> getAll(final Set<T> elements, final Predicate<? super T>... predicates) {
        return (Set<T>)(Utils.isEmpty(predicates) ? elements : Sets.newHashSet((Iterable<?>)Iterables.filter((Iterable<? extends E>)elements, Predicates.and((Predicate<? super E>[])predicates))));
    }
    
    public static <T extends Member> Predicate<T> withName(final String name) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.getName().equals(name);
            }
        };
    }
    
    public static <T extends Member> Predicate<T> withPrefix(final String prefix) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.getName().startsWith(prefix);
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withPattern(final String regex) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return Pattern.matches(regex, input.toString());
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotation(final Class<? extends Annotation> annotation) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.isAnnotationPresent(annotation);
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotations(final Class<? extends Annotation>... annotations) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && Arrays.equals(annotations, annotationTypes(input.getAnnotations()));
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotation(final Annotation annotation) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.isAnnotationPresent(annotation.annotationType()) && areAnnotationMembersMatching(input.getAnnotation(annotation.annotationType()), annotation);
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotations(final Annotation... annotations) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                if (input != null) {
                    final Annotation[] inputAnnotations = input.getAnnotations();
                    if (inputAnnotations.length == annotations.length) {
                        for (int i = 0; i < inputAnnotations.length; ++i) {
                            if (!areAnnotationMembersMatching(inputAnnotations[i], annotations[i])) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        };
    }
    
    public static Predicate<Member> withParameters(final Class<?>... types) {
        return new Predicate<Member>() {
            public boolean apply(@Nullable final Member input) {
                return Arrays.equals(parameterTypes(input), types);
            }
        };
    }
    
    public static Predicate<Member> withParametersAssignableTo(final Class... types) {
        return new Predicate<Member>() {
            public boolean apply(@Nullable final Member input) {
                if (input != null) {
                    final Class<?>[] parameterTypes = (Class<?>[])parameterTypes(input);
                    if (parameterTypes.length == types.length) {
                        for (int i = 0; i < parameterTypes.length; ++i) {
                            if (!parameterTypes[i].isAssignableFrom(types[i]) || (parameterTypes[i] == Object.class && types[i] != Object.class)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
    }
    
    public static Predicate<Member> withParametersCount(final int count) {
        return new Predicate<Member>() {
            public boolean apply(@Nullable final Member input) {
                return input != null && parameterTypes(input).length == count;
            }
        };
    }
    
    public static Predicate<Member> withAnyParameterAnnotation(final Class<? extends Annotation> annotationClass) {
        return new Predicate<Member>() {
            public boolean apply(@Nullable final Member input) {
                return input != null && Iterables.any((Iterable<Object>)annotationTypes(parameterAnnotations(input)), (Predicate<? super Object>)new Predicate<Class<? extends Annotation>>() {
                    public boolean apply(@Nullable final Class<? extends Annotation> input) {
                        return input.equals(annotationClass);
                    }
                });
            }
        };
    }
    
    public static Predicate<Member> withAnyParameterAnnotation(final Annotation annotation) {
        return new Predicate<Member>() {
            public boolean apply(@Nullable final Member input) {
                return input != null && Iterables.any((Iterable<Object>)parameterAnnotations(input), (Predicate<? super Object>)new Predicate<Annotation>() {
                    public boolean apply(@Nullable final Annotation input) {
                        return areAnnotationMembersMatching(annotation, input);
                    }
                });
            }
        };
    }
    
    public static <T> Predicate<Field> withType(final Class<T> type) {
        return new Predicate<Field>() {
            public boolean apply(@Nullable final Field input) {
                return input != null && input.getType().equals(type);
            }
        };
    }
    
    public static <T> Predicate<Field> withTypeAssignableTo(final Class<T> type) {
        return new Predicate<Field>() {
            public boolean apply(@Nullable final Field input) {
                return input != null && type.isAssignableFrom(input.getType());
            }
        };
    }
    
    public static <T> Predicate<Method> withReturnType(final Class<T> type) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                return input != null && input.getReturnType().equals(type);
            }
        };
    }
    
    public static <T> Predicate<Method> withReturnTypeAssignableTo(final Class<T> type) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                return input != null && type.isAssignableFrom(input.getReturnType());
            }
        };
    }
    
    public static <T extends Member> Predicate<T> withModifier(final int mod) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && (input.getModifiers() & mod) != 0x0;
            }
        };
    }
    
    public static Predicate<Class<?>> withClassModifier(final int mod) {
        return new Predicate<Class<?>>() {
            public boolean apply(@Nullable final Class<?> input) {
                return input != null && (input.getModifiers() & mod) != 0x0;
            }
        };
    }
    
    public static Class<?> forName(final String typeName, final ClassLoader... classLoaders) {
        if (getPrimitiveNames().contains(typeName)) {
            return getPrimitiveTypes().get(getPrimitiveNames().indexOf(typeName));
        }
        String type;
        if (typeName.contains("[")) {
            final int i = typeName.indexOf("[");
            type = typeName.substring(0, i);
            final String array = typeName.substring(i).replace("]", "");
            if (getPrimitiveNames().contains(type)) {
                type = getPrimitiveDescriptors().get(getPrimitiveNames().indexOf(type));
            }
            else {
                type = "L" + type + ";";
            }
            type = array + type;
        }
        else {
            type = typeName;
        }
        final List<ReflectionsException> reflectionsExceptions = (List<ReflectionsException>)Lists.newArrayList();
        final ClassLoader[] arr$ = ClasspathHelper.classLoaders(classLoaders);
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ClassLoader classLoader = arr$[i$];
            if (type.contains("[")) {
                try {
                    return Class.forName(type, false, classLoader);
                }
                catch (Throwable e) {
                    reflectionsExceptions.add(new ReflectionsException("could not get type for name " + typeName, e));
                }
            }
            try {
                return classLoader.loadClass(type);
            }
            catch (Throwable e) {
                reflectionsExceptions.add(new ReflectionsException("could not get type for name " + typeName, e));
                ++i$;
                continue;
            }
            break;
        }
        if (Reflections.log != null) {
            for (final ReflectionsException reflectionsException : reflectionsExceptions) {
                Reflections.log.debug("could not get type for name " + typeName + " from any class loader", reflectionsException);
            }
        }
        throw new ReflectionsException("could not get type for name " + typeName);
    }
    
    public static <T> List<Class<? extends T>> forNames(final Iterable<String> classes, final ClassLoader[] classLoaders) {
        final List<Class<? extends T>> result = new ArrayList<Class<? extends T>>();
        for (final String className : classes) {
            result.add((Class<? extends T>)forName(className, classLoaders));
        }
        return result;
    }
    
    private static Class[] parameterTypes(final Member member) {
        return (Class[])((member != null) ? ((member.getClass() == Method.class) ? ((Method)member).getParameterTypes() : ((member.getClass() == Constructor.class) ? ((Constructor)member).getParameterTypes() : null)) : null);
    }
    
    private static Set<Annotation> parameterAnnotations(final Member member) {
        final Set<Annotation> result = (Set<Annotation>)Sets.newHashSet();
        final Annotation[][] arr$;
        final Annotation[][] annotations = arr$ = ((member instanceof Method) ? ((Method)member).getParameterAnnotations() : ((member instanceof Constructor) ? ((Constructor)member).getParameterAnnotations() : null));
        for (final Annotation[] annotation : arr$) {
            Collections.addAll(result, annotation);
        }
        return result;
    }
    
    private static Set<Class<? extends Annotation>> annotationTypes(final Iterable<Annotation> annotations) {
        final Set<Class<? extends Annotation>> result = (Set<Class<? extends Annotation>>)Sets.newHashSet();
        for (final Annotation annotation : annotations) {
            result.add(annotation.annotationType());
        }
        return result;
    }
    
    private static Class<? extends Annotation>[] annotationTypes(final Annotation[] annotations) {
        final Class<? extends Annotation>[] result = (Class<? extends Annotation>[])new Class[annotations.length];
        for (int i = 0; i < annotations.length; ++i) {
            result[i] = annotations[i].annotationType();
        }
        return result;
    }
    
    private static void initPrimitives() {
        if (ReflectionUtils.primitiveNames == null) {
            ReflectionUtils.primitiveNames = Lists.newArrayList("boolean", "char", "byte", "short", "int", "long", "float", "double", "void");
            ReflectionUtils.primitiveTypes = (List<Class>)Lists.newArrayList(Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE);
            ReflectionUtils.primitiveDescriptors = Lists.newArrayList("Z", "C", "B", "S", "I", "J", "F", "D", "V");
        }
    }
    
    private static List<String> getPrimitiveNames() {
        initPrimitives();
        return ReflectionUtils.primitiveNames;
    }
    
    private static List<Class> getPrimitiveTypes() {
        initPrimitives();
        return ReflectionUtils.primitiveTypes;
    }
    
    private static List<String> getPrimitiveDescriptors() {
        initPrimitives();
        return ReflectionUtils.primitiveDescriptors;
    }
    
    static <T> Set<T> filter(final T[] elements, final Predicate<? super T>... predicates) {
        return Utils.isEmpty(predicates) ? Sets.newHashSet(elements) : Sets.newHashSet((Iterable<? extends T>)Iterables.filter((Iterable<? extends E>)Arrays.asList(elements), Predicates.and((Predicate<? super E>[])predicates)));
    }
    
    static <T> Set<T> filter(final Iterable<T> elements, final Predicate<? super T>... predicates) {
        return (Set<T>)(Utils.isEmpty(predicates) ? Sets.newHashSet((Iterable<?>)elements) : Sets.newHashSet((Iterable<?>)Iterables.filter((Iterable<? extends E>)elements, Predicates.and((Predicate<? super E>[])predicates))));
    }
    
    private static boolean areAnnotationMembersMatching(final Annotation annotation1, final Annotation annotation2) {
        if (annotation2 != null && annotation1.annotationType() == annotation2.annotationType()) {
            for (final Method method : annotation1.annotationType().getDeclaredMethods()) {
                try {
                    if (!method.invoke(annotation1, new Object[0]).equals(method.invoke(annotation2, new Object[0]))) {
                        return false;
                    }
                }
                catch (Exception e) {
                    throw new ReflectionsException(String.format("could not invoke method %s on annotation %s", method.getName(), annotation1.annotationType()), e);
                }
            }
            return true;
        }
        return false;
    }
    
    static {
        ReflectionUtils.includeObject = false;
    }
}
