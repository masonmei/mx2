// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.gaffer;

import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import groovy.lang.MetaClass;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.GStringImpl;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;

public class AppenderDelegate extends ComponentDelegate
{
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885841;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public AppenderDelegate(final Appender appender) {
        $getCallSiteArray();
        super(appender);
    }
    
    public String getLabel() {
        $getCallSiteArray();
        return "appender";
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        AppenderDelegate.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        AppenderDelegate.__timeStamp__239_neverHappen1354662885841 = 0L;
        AppenderDelegate.__timeStamp = 1354662885841L;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        return new CallSiteArray((Class)AppenderDelegate.class, new String[0]);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (AppenderDelegate.$callSiteArray == null || ($createCallSiteArray = AppenderDelegate.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            AppenderDelegate.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
