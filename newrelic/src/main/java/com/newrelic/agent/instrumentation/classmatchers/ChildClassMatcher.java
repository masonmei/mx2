// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.util.Strings;
import java.util.Set;

public class ChildClassMatcher extends ClassMatcher
{
    private final String internalSuperClassName;
    private final String superClassName;
    private final boolean onlyMatchChildren;
    private final Set<String> classesToMatch;
    
    public ChildClassMatcher(final String superClassName) {
        this(superClassName, true);
    }
    
    public ChildClassMatcher(final String superClassName, final boolean onlyMatchChildren) {
        this(superClassName, onlyMatchChildren, null);
    }
    
    public ChildClassMatcher(String superClassName, final boolean onlyMatchChildren, final String[] specificChildClasses) {
        superClassName = Strings.fixInternalClassName(superClassName);
        if (superClassName.indexOf(47) < 0) {
            throw new RuntimeException("Invalid class name format");
        }
        this.superClassName = Type.getObjectType(superClassName).getClassName();
        this.internalSuperClassName = superClassName;
        this.onlyMatchChildren = onlyMatchChildren;
        (this.classesToMatch = new HashSet<String>()).add(this.internalSuperClassName);
        if (specificChildClasses != null) {
            this.classesToMatch.addAll(Arrays.asList(specificChildClasses));
        }
    }
    
    public boolean isMatch(ClassLoader loader, final ClassReader cr) {
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        if (cr.getClassName().equals(this.internalSuperClassName)) {
            return !this.onlyMatchChildren;
        }
        return this.isSuperMatch(loader, cr.getSuperName());
    }
    
    private boolean isSuperMatch(final ClassLoader loader, String superName) {
        while (!superName.equals(this.internalSuperClassName)) {
            final URL resource = Utils.getClassResource(loader, superName);
            if (resource == null) {
                return false;
            }
            try {
                final InputStream inputStream = resource.openStream();
                try {
                    final ClassReader cr = new ClassReader(inputStream);
                    superName = cr.getSuperName();
                    inputStream.close();
                }
                finally {
                    inputStream.close();
                }
            }
            catch (IOException ex) {
                return false;
            }
            if (superName == null) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isMatch(Class<?> clazz) {
        if (clazz.getName().equals(this.superClassName)) {
            return !this.onlyMatchChildren;
        }
        while ((clazz = clazz.getSuperclass()) != null) {
            if (clazz.getName().equals(this.superClassName)) {
                return true;
            }
        }
        return false;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.internalSuperClassName == null) ? 0 : this.internalSuperClassName.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ChildClassMatcher other = (ChildClassMatcher)obj;
        if (this.internalSuperClassName == null) {
            if (other.internalSuperClassName != null) {
                return false;
            }
        }
        else if (!this.internalSuperClassName.equals(other.internalSuperClassName)) {
            return false;
        }
        return true;
    }
    
    public Collection<String> getClassNames() {
        return this.classesToMatch;
    }
}
