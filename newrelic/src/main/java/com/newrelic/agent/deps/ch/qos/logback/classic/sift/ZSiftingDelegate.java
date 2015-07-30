// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.GStringImpl;
import com.newrelic.agent.deps.ch.qos.logback.classic.gaffer.AppenderDelegate;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import java.lang.ref.SoftReference;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import groovy.lang.GroovyObject;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class ZSiftingDelegate extends ContextAwareBase implements GroovyObject
{
    private String key;
    private String value;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885822;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public ZSiftingDelegate(final String key, final String value) {
        $getCallSiteArray();
        this.metaClass = this.$getStaticMetaClass();
        this.key = (String)ScriptBytecodeAdapter.castToType((Object)key, (Class)String.class);
        this.value = (String)ScriptBytecodeAdapter.castToType((Object)value, (Class)String.class);
    }
    
    public Appender appender(final String name, final Class clazz, final Closure closure) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        $getCallSiteArray[0].callCurrent((GroovyObject)this, $getCallSiteArray[1].call($getCallSiteArray[2].call((Object)"About to instantiate appender of type [", $getCallSiteArray[3].callGetProperty((Object)clazz)), (Object)"]"));
        final Appender appender = (Appender)ScriptBytecodeAdapter.castToType($getCallSiteArray[4].call((Object)clazz), (Class)Appender.class);
        $getCallSiteArray[5].callCurrent((GroovyObject)this, $getCallSiteArray[6].call($getCallSiteArray[7].call((Object)"Naming appender as [", (Object)name), (Object)"]"));
        ScriptBytecodeAdapter.setProperty((Object)name, (Class)null, (Object)appender, "name");
        ScriptBytecodeAdapter.setProperty($getCallSiteArray[8].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)appender, "context");
        if (BytecodeInterface8.isOrigZ() && !ZSiftingDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, (Object)null)) {
                final AppenderDelegate ad = (AppenderDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[16].callConstructor((Object)AppenderDelegate.class, (Object)appender), (Class)AppenderDelegate.class);
                ScriptBytecodeAdapter.setProperty((Object)this.value, (Class)null, $getCallSiteArray[17].callGetProperty((Object)ad), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { this.key }, new String[] { "", "" }), (Class)String.class));
                $getCallSiteArray[18].call($getCallSiteArray[19].callGetProperty((Object)ad), (Object)new GStringImpl(new Object[] { this.key }, new String[] { "", "" }));
                ScriptBytecodeAdapter.setProperty($getCallSiteArray[20].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)ad, "context");
                ScriptBytecodeAdapter.setGroovyObjectProperty((Object)ad, (Class)ZSiftingDelegate.class, (GroovyObject)closure, "delegate");
                ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[21].callGetProperty((Object)Closure.class), (Class)ZSiftingDelegate.class, (GroovyObject)closure, "resolveStrategy");
                $getCallSiteArray[22].call((Object)closure);
            }
        }
        else if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, (Object)null)) {
            final AppenderDelegate ad2 = (AppenderDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[9].callConstructor((Object)AppenderDelegate.class, (Object)appender), (Class)AppenderDelegate.class);
            ScriptBytecodeAdapter.setProperty((Object)this.value, (Class)null, $getCallSiteArray[10].callGetProperty((Object)ad2), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { this.key }, new String[] { "", "" }), (Class)String.class));
            $getCallSiteArray[11].call($getCallSiteArray[12].callGetProperty((Object)ad2), (Object)new GStringImpl(new Object[] { this.key }, new String[] { "", "" }));
            ScriptBytecodeAdapter.setProperty($getCallSiteArray[13].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)ad2, "context");
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)ad2, (Class)ZSiftingDelegate.class, (GroovyObject)closure, "delegate");
            ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[14].callGetProperty((Object)Closure.class), (Class)ZSiftingDelegate.class, (GroovyObject)closure, "resolveStrategy");
            $getCallSiteArray[15].call((Object)closure);
        }
        $getCallSiteArray[23].call((Object)appender);
        return appender;
    }
    
    public Appender appender(final String name, final Class clazz) {
        $getCallSiteArray();
        return this.appender(name, clazz, null);
    }
    
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ZSiftingDelegate.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo $staticClassInfo = ZSiftingDelegate.$staticClassInfo;
        if ($staticClassInfo == null) {
            $staticClassInfo = (ZSiftingDelegate.$staticClassInfo = ClassInfo.getClassInfo((Class)this.getClass()));
        }
        return $staticClassInfo.getMetaClass();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        ZSiftingDelegate.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        ZSiftingDelegate.__timeStamp__239_neverHappen1354662885822 = 0L;
        ZSiftingDelegate.__timeStamp = 1354662885822L;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[24];
        $createCallSiteArray_1(array);
        return new CallSiteArray((Class)ZSiftingDelegate.class, array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (ZSiftingDelegate.$callSiteArray == null || ($createCallSiteArray = ZSiftingDelegate.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            ZSiftingDelegate.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
