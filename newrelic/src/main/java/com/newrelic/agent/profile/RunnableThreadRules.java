// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.lang.management.ThreadInfo;

public class RunnableThreadRules
{
    public boolean isRunnable(final ThreadInfo threadInfo) {
        return Thread.State.RUNNABLE.equals(threadInfo.getThreadState()) && this.isRunnable(threadInfo.getStackTrace());
    }
    
    public boolean isRunnable(final StackTraceElement[] elements) {
        return elements.length != 0 && this.isRunnable(elements[0]);
    }
    
    public boolean isRunnable(final StackTraceElement firstElement) {
        final String className = firstElement.getClassName();
        final String methodName = firstElement.getMethodName();
        return (!Object.class.getName().equals(className) || !"wait".equals(methodName)) && (!firstElement.isNativeMethod() || (!className.startsWith("java.io.") && !className.startsWith("java.net.") && !className.startsWith("sun.nio.") && !"jrockit.net.SocketNativeIO".equals(className) && (!"java.lang.UNIXProcess".equals(className) || !"waitForProcessExit".equals(methodName)) && (!"sun.misc.Unsafe".equals(className) || !"park".equals(methodName)) && (!"org.apache.tomcat.jni.Socket".equals(className) || !"accept".equals(methodName)) && (!"org.apache.tomcat.jni.Poll".equals(className) || !"poll".equals(methodName)) && (!"weblogic.socket.PosixSocketMuxer".equals(className) || !"poll".equals(methodName)) && (!"weblogic.socket.NTSocketMuxer".equals(className) || !"getIoCompletionResult".equals(methodName)) && (!"com.caucho.vfs.JniServerSocketImpl".equals(className) || !"nativeAccept".equals(methodName))));
    }
}
