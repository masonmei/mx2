// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.classmatchers;

import java.util.Arrays;
import java.util.Collection;
import java.io.IOException;
import java.util.logging.Level;
import com.newrelic.agent.util.asm.BenignClassReadException;
import com.newrelic.agent.util.asm.MissingResourceException;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

public class InterfaceMatcher extends ClassMatcher
{
    private final Type type;
    private final String internalName;
    
    public InterfaceMatcher(final String interfaceName) {
        this.type = Type.getObjectType(Strings.fixInternalClassName(interfaceName));
        this.internalName = this.type.getInternalName();
    }
    
    public boolean isMatch(ClassLoader loader, ClassReader cr) {
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        if ((cr.getAccess() & 0x200) != 0x0) {
            return false;
        }
        if (Utils.getClassResource(loader, this.type) == null) {
            return false;
        }
        final String[] interfaces = cr.getInterfaces();
        if (this.isInterfaceMatch(loader, interfaces)) {
            return true;
        }
        final String superName = cr.getSuperName();
        if (superName != null && !superName.equals("java/lang/Object")) {
            try {
                cr = Utils.readClass(loader, superName);
                return this.isMatch(loader, cr);
            }
            catch (MissingResourceException e) {
                if (Agent.LOG.isFinestEnabled()) {
                    Agent.LOG.finest(MessageFormat.format("Unable to load class {0}: {1}", superName, e));
                }
            }
            catch (BenignClassReadException ex2) {}
            catch (IOException ex) {
                Agent.LOG.log(Level.FINEST, "Unable to match " + this.internalName, ex);
            }
        }
        return false;
    }
    
    private boolean isInterfaceMatch(final ClassLoader loader, final String[] interfaces) {
        if (this.isNameMatch(interfaces)) {
            return true;
        }
        for (final String interfaceName : interfaces) {
            if (this.isInterfaceMatch(loader, interfaceName)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isNameMatch(final String[] interfaces) {
        for (final String interfaceName : interfaces) {
            if (this.internalName.equals(interfaceName)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isInterfaceMatch(final ClassLoader loader, final String interfaceName) {
        try {
            final ClassReader reader = Utils.readClass(loader, interfaceName);
            return this.isInterfaceMatch(loader, reader.getInterfaces());
        }
        catch (MissingResourceException e) {
            if (Agent.LOG.isFinestEnabled()) {
                Agent.LOG.finest(MessageFormat.format("Unable to load interface {0}: {1}", interfaceName, e));
            }
            return false;
        }
        catch (BenignClassReadException e3) {
            return false;
        }
        catch (Exception e2) {
            final String msg = MessageFormat.format("Unable to load interface {0}: {1}", interfaceName, e2);
            if (Agent.LOG.isFinestEnabled()) {
                if (interfaceName.startsWith("com/newrelic/agent/")) {
                    Agent.LOG.log(Level.FINEST, msg);
                }
                else {
                    Agent.LOG.log(Level.FINEST, msg, e2);
                }
            }
            else {
                Agent.LOG.finer(msg);
            }
            return false;
        }
    }
    
    public boolean isMatch(final Class<?> clazz) {
        try {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            if (Utils.getClassResource(classLoader, this.type) == null) {
                return false;
            }
            final Class<?> interfaceClass = classLoader.loadClass(this.type.getClassName());
            if (interfaceClass.isInterface() && interfaceClass.isAssignableFrom(clazz)) {
                return true;
            }
        }
        catch (Exception ex) {}
        catch (Error error) {}
        return false;
    }
    
    public String toString() {
        return this.internalName;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
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
        final InterfaceMatcher other = (InterfaceMatcher)obj;
        if (this.type == null) {
            return other.type == null;
        }
        return this.type.equals(other.type);
    }
    
    public Collection<String> getClassNames() {
        return Arrays.asList(this.internalName);
    }
}
