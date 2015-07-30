// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Arrays;
import com.newrelic.agent.deps.com.google.common.collect.Multimaps;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.com.google.common.collect.SetMultimap;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.com.google.common.base.Supplier;
import java.util.Collection;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;

public final class OptimizedClassMatcher implements ClassMatchVisitorFactory
{
    public static final Set<Method> METHODS_WE_NEVER_INSTRUMENT;
    static final OptimizedClassMatcher EMPTY_MATCHER;
    final Map.Entry<MethodMatcher, ClassAndMethodMatcher>[] methodMatchers;
    final Map<Method, Collection<ClassAndMethodMatcher>> methods;
    final Set<String> methodAnnotationsToMatch;
    Set<String> exactClassNames;
    public static final Method DEFAULT_CONSTRUCTOR;
    static final Supplier<Set<String>> STRING_COLLECTION_SUPPLIER;
    
    private OptimizedClassMatcher() {
        this.methodAnnotationsToMatch = (Set<String>)ImmutableSet.of();
        this.methodMatchers = (Map.Entry<MethodMatcher, ClassAndMethodMatcher>[])new Map.Entry[0];
        this.methods = (Map<Method, Collection<ClassAndMethodMatcher>>)ImmutableMap.of();
    }
    
    protected OptimizedClassMatcher(final Set<String> annotationMatchers, final SetMultimap<Method, ClassAndMethodMatcher> methods, final SetMultimap<MethodMatcher, ClassAndMethodMatcher> methodMatchers, final Set<String> exactClassNames) {
        this.methodAnnotationsToMatch = (Set<String>)ImmutableSet.copyOf((Collection<?>)annotationMatchers);
        this.methodMatchers = methodMatchers.entries().toArray(new Map.Entry[0]);
        this.methods = (Map<Method, Collection<ClassAndMethodMatcher>>)ImmutableMap.copyOf((Map<?, ?>)methods.asMap());
        this.exactClassNames = (Set<String>)((exactClassNames == null) ? null : ImmutableSet.copyOf((Collection<?>)exactClassNames));
    }
    
    public ClassVisitor newClassMatchVisitor(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final ClassVisitor cv, final InstrumentationContext context) {
        if (this.exactClassNames != null && !this.exactClassNames.contains(reader.getClassName())) {
            return null;
        }
        return new ClassMethods(loader, reader, (Class)classBeingRedefined, cv, context);
    }
    
    private Multimap<ClassAndMethodMatcher, String> newClassMatches() {
        return (Multimap<ClassAndMethodMatcher, String>)Multimaps.newSetMultimap((Map<Object, Collection<Object>>)Maps.newHashMap(), (Supplier<? extends Set<Object>>)OptimizedClassMatcher.STRING_COLLECTION_SUPPLIER);
    }
    
    public String toString() {
        return "OptimizedClassMatcher [methodMatchers=" + Arrays.toString(this.methodMatchers) + ", methods=" + this.methods + ", methodAnnotationsToMatch=" + this.methodAnnotationsToMatch + ", exactClassNames=" + this.exactClassNames + "]";
    }
    
    static {
        METHODS_WE_NEVER_INSTRUMENT = ImmutableSet.of(new Method("equals", "(Ljava/lang/Object;)Z"), new Method("toString", "()Ljava/lang/String;"), new Method("finalize", "()V"), new Method("hashCode", "()I"));
        EMPTY_MATCHER = new OptimizedClassMatcher();
        DEFAULT_CONSTRUCTOR = new Method("<init>", "()V");
        STRING_COLLECTION_SUPPLIER = new Supplier<Set<String>>() {
            public Set<String> get() {
                return (Set<String>)Sets.newHashSet();
            }
        };
    }
    
    public static final class Match
    {
        private final Map<ClassAndMethodMatcher, Collection<String>> classNames;
        private final Set<Method> methods;
        private final Map<Method, Set<String>> methodAnnotations;
        
        public Match(final Multimap<ClassAndMethodMatcher, String> classMatches, final Set<Method> methods, final Map<Method, Set<String>> methodAnnotations) {
            this.classNames = (Map<ClassAndMethodMatcher, Collection<String>>)ImmutableMap.copyOf((Map<?, ?>)classMatches.asMap());
            this.methods = (Set<Method>)ImmutableSet.copyOf((Collection<?>)methods);
            this.methodAnnotations = (Map<Method, Set<String>>)((methodAnnotations == null) ? ImmutableMap.of() : methodAnnotations);
        }
        
        public Map<ClassAndMethodMatcher, Collection<String>> getClassMatches() {
            return this.classNames;
        }
        
        public Set<Method> getMethods() {
            return this.methods;
        }
        
        public Set<String> getMethodAnnotations(final Method method) {
            final Set<String> set = this.methodAnnotations.get(method);
            return (Set<String>)((set == null) ? ImmutableSet.of() : set);
        }
        
        public boolean isClassAndMethodMatch() {
            return !this.methods.isEmpty() && !this.classNames.isEmpty();
        }
        
        public String toString() {
            return this.classNames.toString() + " methods " + this.methods;
        }
    }
    
    private class ClassMethods extends ClassVisitor
    {
        private final Class<?> classBeingRedefined;
        private final ClassReader cr;
        private final ClassLoader loader;
        private SetMultimap<Method, ClassAndMethodMatcher> matches;
        private Map<Method, Set<String>> methodAnnotations;
        private Map<ClassMatcher, Boolean> classMatcherMatches;
        private final InstrumentationContext context;
        
        private ClassMethods(final ClassLoader loader, final ClassReader cr, final Class<?> classBeingRedefined, final ClassVisitor cv, final InstrumentationContext context) {
            super(327680, cv);
            this.cr = cr;
            this.classBeingRedefined = classBeingRedefined;
            this.loader = loader;
            this.context = context;
        }
        
        private void addMethodAnnotations(final Method method, final Set<String> annotations) {
            if (!annotations.isEmpty()) {
                if (this.methodAnnotations == null) {
                    this.methodAnnotations = (Map<Method, Set<String>>)Maps.newHashMap();
                }
                this.methodAnnotations.put(method, annotations);
            }
        }
        
        private SetMultimap<Method, ClassAndMethodMatcher> getOrCreateMatches() {
            if (this.matches == null) {
                this.matches = Multimaps.newSetMultimap((Map<Method, Collection<ClassAndMethodMatcher>>)Maps.newHashMap(), new Supplier<Set<ClassAndMethodMatcher>>() {
                    public Set<ClassAndMethodMatcher> get() {
                        return (Set<ClassAndMethodMatcher>)Sets.newHashSet();
                    }
                });
            }
            return this.matches;
        }
        
        private boolean isMatch(final ClassMatcher classMatcher, final ClassLoader loader, final ClassReader cr, final Class<?> classBeingRedefined) {
            if (null == this.classMatcherMatches) {
                this.classMatcherMatches = (Map<ClassMatcher, Boolean>)Maps.newHashMap();
            }
            Boolean match = this.classMatcherMatches.get(classMatcher);
            if (match == null) {
                match = ((classBeingRedefined == null) ? classMatcher.isMatch(loader, cr) : classMatcher.isMatch(classBeingRedefined));
                this.classMatcherMatches.put(classMatcher, match);
            }
            return match;
        }
        
        public MethodVisitor visitMethod(final int access, final String methodName, final String methodDesc, final String signature, final String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, methodName, methodDesc, signature, exceptions);
            if ((access & 0x400) == 0x0 && (access & 0x100) == 0x0) {
                final Method method = new Method(methodName, methodDesc);
                if (OptimizedClassMatcher.METHODS_WE_NEVER_INSTRUMENT.contains(method)) {
                    return mv;
                }
                if (!OptimizedClassMatcher.this.methodAnnotationsToMatch.isEmpty()) {
                    mv = new MethodVisitor(327680, mv) {
                        final Set<String> annotations = Sets.newHashSet();
                        
                        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                            if (OptimizedClassMatcher.this.methodAnnotationsToMatch.contains(desc)) {
                                this.annotations.add(desc);
                            }
                            return super.visitAnnotation(desc, visible);
                        }
                        
                        public void visitEnd() {
                            super.visitEnd();
                            ClassMethods.this.addMethodAnnotations(method, this.annotations);
                            if (ClassMethods.this.addMethodIfMatches(access, method, this.annotations) && (access & 0x40) != 0x0) {
                                ClassMethods.this.context.addBridgeMethod(method);
                            }
                        }
                    };
                }
                else if (this.addMethodIfMatches(access, method, (Set<String>)ImmutableSet.of()) && (access & 0x40) != 0x0) {
                    this.context.addBridgeMethod(method);
                }
            }
            return mv;
        }
        
        public void visitEnd() {
            super.visitEnd();
            if (this.matches != null) {
                final Multimap<ClassAndMethodMatcher, String> classMatches = OptimizedClassMatcher.this.newClassMatches();
                for (final ClassAndMethodMatcher matcher : this.matches.values()) {
                    for (final String className : matcher.getClassMatcher().getClassNames()) {
                        classMatches.put(matcher, className);
                    }
                    classMatches.put(matcher, this.cr.getClassName());
                }
                final Set<Method> daMethods = this.matches.keySet();
                final Match match = new Match(classMatches, daMethods, this.methodAnnotations);
                this.context.putMatch(OptimizedClassMatcher.this, match);
            }
        }
        
        private boolean addMethodIfMatches(final int access, final Method method, final Set<String> annotations) {
            boolean match = false;
            final Collection<ClassAndMethodMatcher> set = OptimizedClassMatcher.this.methods.get(method);
            if (set != null) {
                for (final ClassAndMethodMatcher matcher : set) {
                    if (this.isMatch(matcher.getClassMatcher(), this.loader, this.cr, this.classBeingRedefined)) {
                        this.getOrCreateMatches().put(method, matcher);
                        match = true;
                    }
                }
            }
            for (final Map.Entry<MethodMatcher, ClassAndMethodMatcher> entry : OptimizedClassMatcher.this.methodMatchers) {
                if (entry.getKey().matches(access, method.getName(), method.getDescriptor(), annotations) && this.isMatch(entry.getValue().getClassMatcher(), this.loader, this.cr, this.classBeingRedefined)) {
                    this.getOrCreateMatches().put(method, entry.getValue());
                    match = true;
                }
            }
            return match;
        }
    }
}
