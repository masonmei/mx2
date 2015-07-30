// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.gaffer;

import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.GeneratedClosure;
import groovy.lang.Reference;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import com.newrelic.agent.deps.ch.qos.logback.core.util.ContextUtil;
import groovy.lang.Binding;
import java.io.File;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import java.net.URL;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import java.lang.ref.SoftReference;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import groovy.lang.GroovyObject;

public class GafferConfigurator implements GroovyObject
{
    private LoggerContext context;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885722;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public GafferConfigurator(final LoggerContext context) {
        $getCallSiteArray();
        this.metaClass = this.$getStaticMetaClass();
        this.context = (LoggerContext)ScriptBytecodeAdapter.castToType((Object)context, (Class)LoggerContext.class);
    }
    
    protected void informContextOfURLUsedForConfiguration(final URL url) {
        $getCallSiteArray()[0].call((Object)ConfigurationWatchListUtil.class, (Object)this.context, (Object)url);
    }
    
    public void run(final URL url) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (!GafferConfigurator.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            this.informContextOfURLUsedForConfiguration(url);
        }
        else {
            $getCallSiteArray[1].callCurrent((GroovyObject)this, (Object)url);
        }
        $getCallSiteArray[2].callCurrent((GroovyObject)this, $getCallSiteArray[3].callGetProperty((Object)url));
    }
    
    public void run(final File file) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        $getCallSiteArray[4].callCurrent((GroovyObject)this, $getCallSiteArray[5].call($getCallSiteArray[6].call((Object)file)));
        $getCallSiteArray[7].callCurrent((GroovyObject)this, $getCallSiteArray[8].callGetProperty((Object)file));
    }
    
    public void run(final String dslText) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final Binding binding = (Binding)ScriptBytecodeAdapter.castToType($getCallSiteArray[9].callConstructor((Object)Binding.class), (Class)Binding.class);
        $getCallSiteArray[10].call((Object)binding, (Object)"hostname", $getCallSiteArray[11].call((Object)ContextUtil.class));
        final Reference dslScript = new Reference((Object)ScriptBytecodeAdapter.castToType($getCallSiteArray[12].call($getCallSiteArray[13].callConstructor((Object)GroovyShell.class, (Object)binding), (Object)dslText), (Class)Script.class));
        $getCallSiteArray[14].call($getCallSiteArray[15].callGroovyObjectGetProperty((Object)dslScript.get()), (Object)ConfigurationDelegate.class);
        $getCallSiteArray[16].call((Object)dslScript.get(), (Object)this.context);
        ScriptBytecodeAdapter.setProperty((Object)new GeneratedClosure((Object)this, (Object)this) {
            private static /* synthetic */ SoftReference $callSiteArray;
            
            public Object doCall(final Object it) {
                $getCallSiteArray();
                return dslScript.get();
            }
            
            public Script getDslScript() {
                $getCallSiteArray();
                return (Script)ScriptBytecodeAdapter.castToType(dslScript.get(), (Class)Script.class);
            }
            
            public Object doCall() {
                $getCallSiteArray();
                return this.doCall(null);
            }
            
            public static /* synthetic */ void __$swapInit() {
                $getCallSiteArray();
                GafferConfigurator$_run_closure1.$callSiteArray = null;
            }
            
            static {
                __$swapInit();
            }
            
            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                return new CallSiteArray((Class)GafferConfigurator$_run_closure1.class, new String[0]);
            }
            
            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray $createCallSiteArray;
                if (GafferConfigurator$_run_closure1.$callSiteArray == null || ($createCallSiteArray = GafferConfigurator$_run_closure1.$callSiteArray.get()) == null) {
                    $createCallSiteArray = $createCallSiteArray();
                    GafferConfigurator$_run_closure1.$callSiteArray = new SoftReference($createCallSiteArray);
                }
                return $createCallSiteArray.array;
            }
        }, (Class)null, $getCallSiteArray[17].callGroovyObjectGetProperty((Object)dslScript.get()), "getDeclaredOrigin");
        $getCallSiteArray[18].call((Object)dslScript.get());
    }
    
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GafferConfigurator.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo $staticClassInfo = GafferConfigurator.$staticClassInfo;
        if ($staticClassInfo == null) {
            $staticClassInfo = (GafferConfigurator.$staticClassInfo = ClassInfo.getClassInfo((Class)this.getClass()));
        }
        return $staticClassInfo.getMetaClass();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        GafferConfigurator.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        GafferConfigurator.__timeStamp__239_neverHappen1354662885722 = 0L;
        GafferConfigurator.__timeStamp = 1354662885722L;
    }
    
    public LoggerContext getContext() {
        return this.context;
    }
    
    public void setContext(final LoggerContext context) {
        this.context = context;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[19];
        $createCallSiteArray_1(array);
        return new CallSiteArray((Class)GafferConfigurator.class, array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (GafferConfigurator.$callSiteArray == null || ($createCallSiteArray = GafferConfigurator.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            GafferConfigurator.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
