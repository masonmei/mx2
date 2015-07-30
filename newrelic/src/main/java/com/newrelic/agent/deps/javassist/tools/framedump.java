// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.tools;

import com.newrelic.agent.deps.javassist.CtClass;
import com.newrelic.agent.deps.javassist.bytecode.analysis.FramePrinter;
import com.newrelic.agent.deps.javassist.ClassPool;

public class framedump
{
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java javassist.tools.framedump <class file name>");
            return;
        }
        final ClassPool pool = ClassPool.getDefault();
        final CtClass clazz = pool.get(args[0]);
        System.out.println("Frame Dump of " + clazz.getName() + ":");
        FramePrinter.print(clazz, System.out);
    }
}
