// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class NewClassDependencyVisitor extends ClassVisitor
{
    protected String className;
    protected final List<String> newClassLoadOrder;
    protected Set<String> referencedSupertypes;
    
    public NewClassDependencyVisitor(final int api, final ClassVisitor cv, final List<String> newClassLoadOrder) {
        super(api, cv);
        this.newClassLoadOrder = newClassLoadOrder;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        (this.referencedSupertypes = (Set<String>)Sets.newHashSet((Iterable<?>)Arrays.asList(interfaces))).add(superName);
    }
    
    public void visitEnd() {
        super.visitEnd();
        final int classIndex = this.newClassLoadOrder.indexOf(this.className);
        if (classIndex < 0) {
            Agent.LOG.log(Level.FINEST, "Error: Visisted class is not in the dependency list: {0}", new Object[] { this.className });
        }
        else {
            this.sortDependencyList(classIndex, this.newClassLoadOrder, this.referencedSupertypes);
        }
    }
    
    private void sortDependencyList(int classIndex, final List<String> dependencies, final Set<String> classDependencies) {
        for (final String dependency : classDependencies) {
            final int dependencyIndex = dependencies.indexOf(dependency);
            if (dependencyIndex > classIndex) {
                Agent.LOG.log(Level.FINEST, "{0} : Moving dependency {1} to position {2}", new Object[] { this.className, dependency, classIndex });
                dependencies.add(classIndex, dependencies.remove(dependencyIndex));
                ++classIndex;
            }
        }
    }
}
