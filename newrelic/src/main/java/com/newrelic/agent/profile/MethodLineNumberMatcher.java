// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.io.InputStream;
import java.io.IOException;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class MethodLineNumberMatcher
{
    public static String getMethodDescription(final Class<?> currentClass, final String mMethodName, final int lineNumber) {
        try {
            if (currentClass != null && mMethodName != null && lineNumber > 0) {
                final ClassReader cr = getClassReader(currentClass);
                final LineNumberClassVisitor cv = new LineNumberClassVisitor(mMethodName, lineNumber);
                cr.accept(cv, 4);
                return cv.getActualMethodDesc();
            }
        }
        catch (Throwable e) {
            Agent.LOG.log(Level.FINEST, "Unable to grab method info using line numbers", e);
        }
        return null;
    }
    
    private static ClassReader getClassReader(final Class<?> currentClass) {
        final ClassLoader loader = (currentClass.getClassLoader() == null) ? ClassLoader.getSystemClassLoader() : currentClass.getClassLoader();
        final String resource = currentClass.getName().replace('.', '/') + ".class";
        InputStream is = null;
        ClassReader cr;
        try {
            is = loader.getResourceAsStream(resource);
            cr = new ClassReader(is);
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ex) {}
            }
        }
        catch (IOException e) {
            throw new RuntimeException("unable to access resource: " + resource, e);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ex2) {}
            }
        }
        return cr;
    }
    
    public static class LineNumberClassVisitor extends ClassVisitor
    {
        private final String methodName;
        private final int lineNumber;
        private String actualMethodDesc;
        
        public LineNumberClassVisitor(final ClassVisitor cv, final String mName, final int lNumber) {
            super(327680, cv);
            this.methodName = mName;
            this.lineNumber = lNumber;
            this.actualMethodDesc = null;
        }
        
        public LineNumberClassVisitor(final String mName, final int lNumber) {
            super(327680);
            this.methodName = mName;
            this.lineNumber = lNumber;
            this.actualMethodDesc = null;
        }
        
        public MethodVisitor visitMethod(final int access, final String pMethodName, final String methodDesc, final String signature, final String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, pMethodName, methodDesc, signature, exceptions);
            if (this.methodName.equals(pMethodName)) {
                mv = new MethodVisitor(327680, mv) {
                    public void visitLineNumber(final int line, final Label start) {
                        super.visitLineNumber(line, start);
                        if (LineNumberClassVisitor.this.lineNumber == line) {
                            LineNumberClassVisitor.this.actualMethodDesc = methodDesc;
                        }
                    }
                };
            }
            return mv;
        }
        
        public boolean foundMethod() {
            return this.actualMethodDesc != null;
        }
        
        public String getActualMethodDesc() {
            return this.actualMethodDesc;
        }
    }
}
