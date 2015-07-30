// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.serializers;

import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Multimaps;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.base.Supplier;
import java.util.Collection;
import java.util.HashMap;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import java.lang.reflect.Method;
import com.newrelic.agent.deps.org.reflections.ReflectionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import com.newrelic.agent.deps.org.reflections.ReflectionsException;
import java.util.LinkedList;
import com.newrelic.agent.deps.com.google.common.base.Joiner;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.org.reflections.scanners.TypeElementsScanner;
import java.io.IOException;
import com.newrelic.agent.deps.com.google.common.io.Files;
import java.nio.charset.Charset;
import java.util.Date;
import com.newrelic.agent.deps.org.reflections.util.Utils;
import java.io.File;
import com.newrelic.agent.deps.org.reflections.Reflections;
import java.io.InputStream;

public class JavaCodeSerializer1 implements Serializer
{
    private static final String pathSeparator = "_";
    private static final String doubleSeparator = "__";
    private static final String dotSeparator = ".";
    private static final String arrayDescriptor = "$$";
    private static final String tokenSeparator = "_";
    
    public Reflections read(final InputStream inputStream) {
        throw new UnsupportedOperationException("read is not implemented on JavaCodeSerializer");
    }
    
    public File save(final Reflections reflections, String name) {
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        final String filename = name.replace('.', '/').concat(".java");
        final File file = Utils.prepareFile(filename);
        final int lastDot = name.lastIndexOf(46);
        String packageName;
        String className;
        if (lastDot == -1) {
            packageName = "";
            className = name.substring(name.lastIndexOf(47) + 1);
        }
        else {
            packageName = name.substring(name.lastIndexOf(47) + 1, lastDot);
            className = name.substring(lastDot + 1);
        }
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append("//generated using Reflections JavaCodeSerializer").append(" [").append(new Date()).append("]").append("\n");
            if (packageName.length() != 0) {
                sb.append("package ").append(packageName).append(";\n");
                sb.append("\n");
            }
            sb.append("public interface ").append(className).append(" {\n\n");
            sb.append(this.toString(reflections));
            sb.append("}\n");
            Files.write(sb.toString(), new File(filename), Charset.defaultCharset());
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        return file;
    }
    
    public String toString(final Reflections reflections) {
        if (reflections.getStore().get(TypeElementsScanner.class.getSimpleName()).isEmpty() && Reflections.log != null) {
            Reflections.log.warn("JavaCodeSerializer needs TypeElementsScanner configured");
        }
        final Writer sb = new Writer();
        String prev = "";
        boolean prevEmpty = false;
        final List<String> keys = (List<String>)Lists.newArrayList((Iterable<?>)reflections.getStore().get(TypeElementsScanner.class.getSimpleName()).keySet());
        Collections.sort(keys);
        for (final String key : keys) {
            final String fqn = key.replace("$", ".");
            final String common = this.commonPath(prev, fqn);
            final String cn = fqn.contains(".") ? fqn.substring(fqn.lastIndexOf(".") + 1) : "";
            if (prev.length() > common.length() + 1) {
                for (final String ignored : prev.substring(common.length() + 1).split("\\.")) {
                    if (!prevEmpty) {
                        sb.indentLeft();
                    }
                    else {
                        prevEmpty = false;
                    }
                    sb.append("}\n");
                }
            }
            if (fqn.length() > common.length()) {
                final String[] split = fqn.substring(common.length()).split("\\.");
                for (int i = 0; i < split.length - 1; ++i) {
                    final String path = split[i];
                    if (path.length() > 0) {
                        sb.indentRight().append("public interface ").append(path).append(" {\n");
                    }
                    else {
                        final Writer writer = sb;
                        --writer.indent;
                    }
                }
            }
            final Data data = new Data(reflections, key);
            sb.indentRight().append("public enum ").append(cn).append(" {");
            if (!data.elements.isEmpty()) {
                sb.append("\n");
                final Writer writer2 = sb;
                ++writer2.indent;
                final int fs = data.fields.size();
                final int ms = data.elements.size() - fs;
                final int ss = fs + ms;
                if (fs > 0) {
                    sb.indent().append("//fields").append("\n");
                    for (int j = 0; j < fs; ++j) {
                        sb.indent().append(data.elements.get(j)).append((j < ss - 1) ? "," : "").append("\n");
                    }
                }
                if (ms > 0) {
                    sb.indent().append("//methods").append("\n");
                    for (int j = fs; j < ss; ++j) {
                        sb.indent().append(data.elements.get(j)).append((j < ss - 1) ? "," : "").append("\n");
                    }
                }
            }
            prevEmpty = data.elements.isEmpty();
            prev = fqn;
        }
        for (final String ignored2 : prev.split("\\.")) {
            sb.indentLeft().append("}\n");
        }
        return sb.toString();
    }
    
    private String commonPath(final String prev, final String fqn) {
        int maxPrefixLength;
        int p;
        for (maxPrefixLength = Math.min(prev.length(), fqn.length()), p = 0; p < maxPrefixLength && prev.charAt(p) == fqn.charAt(p); ++p) {}
        if (this.aBoolean(prev, p) || this.aBoolean(fqn, p)) {
            p = prev.lastIndexOf(".");
        }
        return prev.subSequence(0, p).toString();
    }
    
    private boolean aBoolean(final String prev, final int p) {
        return p - 1 >= 0 && p - 1 <= prev.length() - 1 && prev.charAt(p - 1) != '.';
    }
    
    private String name(final String candidate, final List<String> prev, final int offset) {
        final String normalized = candidate.replace(".", "_");
        for (int i = 0; i < offset; ++i) {
            if (normalized.equals(prev.get(i))) {
                return this.name(normalized + "_", prev, offset);
            }
        }
        return normalized.contains("$") ? Joiner.on(".").join(normalized.split("\\$")) : normalized;
    }
    
    public static Class<?> resolveClassOf(final Class element) throws ClassNotFoundException {
        Class<?> cursor = (Class<?>)element;
        final LinkedList<String> ognl = Lists.newLinkedList();
        while (cursor != null) {
            ognl.addFirst(cursor.getSimpleName());
            cursor = cursor.getDeclaringClass();
        }
        final String classOgnl = Joiner.on(".").join(ognl.subList(1, ognl.size())).replace(".$", "$");
        return Class.forName(classOgnl);
    }
    
    public static Class<?> resolveClass(final Class aClass) {
        try {
            return resolveClassOf(aClass);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not resolve to class " + aClass.getName(), e);
        }
    }
    
    public static Field resolveField(final Class aField) {
        try {
            final String name = aField.getSimpleName();
            final Class<?> declaringClass = (Class<?>)aField.getDeclaringClass().getDeclaringClass();
            return resolveClassOf(declaringClass).getDeclaredField(name);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not resolve to field " + aField.getName(), e);
        }
    }
    
    public static Annotation resolveAnnotation(final Class annotation) {
        try {
            final String name = annotation.getSimpleName().replace("_", ".");
            final Class<?> declaringClass = (Class<?>)annotation.getDeclaringClass().getDeclaringClass();
            final Class<?> aClass = resolveClassOf(declaringClass);
            final Class<? extends Annotation> aClass2 = (Class<? extends Annotation>)ReflectionUtils.forName(name, new ClassLoader[0]);
            return aClass.getAnnotation(aClass2);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not resolve to annotation " + annotation.getName(), e);
        }
    }
    
    public static Method resolveMethod(final Class aMethod) {
        final String methodOgnl = aMethod.getSimpleName();
        try {
            String methodName;
            Class<?>[] paramTypes;
            if (methodOgnl.contains("_")) {
                methodName = methodOgnl.substring(0, methodOgnl.indexOf("_"));
                final String[] params = methodOgnl.substring(methodOgnl.indexOf("_") + 1).split("__");
                paramTypes = (Class<?>[])new Class[params.length];
                for (int i = 0; i < params.length; ++i) {
                    final String typeName = params[i].replace("$$", "[]").replace("_", ".");
                    paramTypes[i] = ReflectionUtils.forName(typeName, new ClassLoader[0]);
                }
            }
            else {
                methodName = methodOgnl;
                paramTypes = null;
            }
            final Class<?> declaringClass = (Class<?>)aMethod.getDeclaringClass().getDeclaringClass();
            return resolveClassOf(declaringClass).getDeclaredMethod(methodName, paramTypes);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not resolve to method " + aMethod.getName(), e);
        }
    }
    
    private static class Writer
    {
        StringBuilder sb;
        int indent;
        
        private Writer() {
            this.sb = new StringBuilder();
            this.indent = 0;
        }
        
        public Writer append(final String s) {
            this.sb.append(s);
            return this;
        }
        
        public Writer indentRight() {
            return this.append(Utils.repeat("\t", ++this.indent));
        }
        
        public Writer indentLeft() {
            final String string = "\t";
            final int n = this.indent - 1;
            this.indent = n;
            return this.append(Utils.repeat(string, n));
        }
        
        public Writer indent() {
            return this.append(Utils.repeat("\t", this.indent));
        }
        
        public String toString() {
            return this.sb.toString();
        }
    }
    
    private class Data
    {
        private List<String> annotations;
        private List<String> fields;
        private Multimap<String, String> methods;
        private List<String> elements;
        
        public Data(final Reflections reflections, final String key) {
            this.annotations = (List<String>)Lists.newArrayList();
            this.fields = (List<String>)Lists.newArrayList();
            this.methods = (Multimap<String, String>)Multimaps.newSetMultimap(new HashMap<Object, Collection<Object>>(), (Supplier<? extends Set<Object>>)new Supplier<Set<String>>() {
                public Set<String> get() {
                    return (Set<String>)Sets.newHashSet();
                }
            });
            for (final String element : reflections.getStore().get(TypeElementsScanner.class.getSimpleName(), key)) {
                if (element.startsWith("@")) {
                    this.annotations.add(element.substring(1));
                }
                else if (element.contains("(")) {
                    if (element.startsWith("<")) {
                        continue;
                    }
                    final int i1 = element.indexOf(40);
                    final String name = element.substring(0, i1);
                    final String params = element.substring(i1 + 1, element.indexOf(")"));
                    String paramsDescriptor = "";
                    if (params.length() != 0) {
                        paramsDescriptor = "_" + params.replace(".", "_").replace(", ", "__").replace("[]", "$$");
                    }
                    final String normalized = name + paramsDescriptor;
                    this.methods.put(name, normalized);
                }
                else {
                    if (Utils.isEmpty(element)) {
                        continue;
                    }
                    this.fields.add(element);
                }
            }
            (this.elements = (List<String>)Lists.newArrayList()).addAll(this.fields);
            for (final String k : this.methods.keySet()) {
                if (!this.elements.contains(k)) {
                    this.elements.add(k);
                }
                else {
                    final List<String> strings = (List<String>)Lists.newArrayList((Iterable<?>)this.methods.get(k));
                    Collections.sort(strings);
                    for (final String string : strings) {
                        this.elements.add(JavaCodeSerializer1.this.name(string, this.elements, 0));
                    }
                }
            }
            for (Map.Entry<String, String> entry : this.methods.entries()) {}
        }
    }
}
