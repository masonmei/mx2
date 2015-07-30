// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.gaffer;

import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.GeneratedClosure;
import groovy.lang.Closure;
import java.beans.Introspector;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import java.lang.ref.SoftReference;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import groovy.lang.GroovyObject;

public class PropertyUtil implements GroovyObject
{
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885762;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public PropertyUtil() {
        $getCallSiteArray();
        this.metaClass = this.$getStaticMetaClass();
    }
    
    public static boolean hasAdderMethod(final Object obj, final String name) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        String addMethod = null;
        if (!PropertyUtil.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            addMethod = (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { upperCaseFirstLetter(name) }, new String[] { "add", "" }), (Class)String.class);
        }
        else {
            addMethod = (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { $getCallSiteArray[0].callStatic((Class)PropertyUtil.class, (Object)name) }, new String[] { "add", "" }), (Class)String.class);
        }
        return DefaultTypeTransformation.booleanUnbox($getCallSiteArray[1].call($getCallSiteArray[2].callGetProperty(obj), obj, (Object)addMethod));
    }
    
    public static NestingType nestingType(final Object obj, final String name) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final Object decapitalizedName = $getCallSiteArray[3].call((Object)Introspector.class, (Object)name);
        if (DefaultTypeTransformation.booleanUnbox($getCallSiteArray[4].call(obj, decapitalizedName))) {
            return (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray[5].callGetProperty((Object)NestingType.class), (Class)NestingType.class);
        }
        if (DefaultTypeTransformation.booleanUnbox($getCallSiteArray[6].callStatic((Class)PropertyUtil.class, obj, (Object)name))) {
            return (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray[7].callGetProperty((Object)NestingType.class), (Class)NestingType.class);
        }
        return (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray[8].callGetProperty((Object)NestingType.class), (Class)NestingType.class);
    }
    
    public static void attach(final NestingType nestingType, final Object component, final Object subComponent, String name) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (ScriptBytecodeAdapter.isCase((Object)nestingType, $getCallSiteArray[9].callGetProperty((Object)NestingType.class))) {
            name = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[10].call((Object)Introspector.class, (Object)name), (Class)String.class);
            ScriptBytecodeAdapter.setProperty(subComponent, (Class)null, component, (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { name }, new String[] { "", "" }), (Class)String.class));
        }
        else if (ScriptBytecodeAdapter.isCase((Object)nestingType, $getCallSiteArray[11].callGetProperty((Object)NestingType.class))) {
            final String firstUpperName = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[12].call((Object)PropertyUtil.class, (Object)name), (Class)String.class);
            ScriptBytecodeAdapter.invokeMethodN((Class)PropertyUtil.class, component, (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { firstUpperName }, new String[] { "add", "" }), (Class)String.class), new Object[] { subComponent });
        }
    }
    
    public static String transformFirstLetter(final String s, final Closure closure) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !PropertyUtil.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)s, (Object)null) || ScriptBytecodeAdapter.compareEqual($getCallSiteArray[14].call((Object)s), (Object)0)) {
                return s;
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)s, (Object)null) || ScriptBytecodeAdapter.compareEqual($getCallSiteArray[13].call((Object)s), (Object)0)) {
            return s;
        }
        final String firstLetter = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[15].callConstructor((Object)String.class, $getCallSiteArray[16].call((Object)s, (Object)0)), (Class)String.class);
        final String modifiedFistLetter = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[17].call((Object)closure, (Object)firstLetter), (Class)String.class);
        if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !PropertyUtil.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual($getCallSiteArray[21].call((Object)s), (Object)1)) {
                return modifiedFistLetter;
            }
            return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[22].call((Object)modifiedFistLetter, $getCallSiteArray[23].call((Object)s, (Object)1)), (Class)String.class);
        }
        else {
            if (ScriptBytecodeAdapter.compareEqual($getCallSiteArray[18].call((Object)s), (Object)1)) {
                return modifiedFistLetter;
            }
            return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[19].call((Object)modifiedFistLetter, $getCallSiteArray[20].call((Object)s, (Object)1)), (Class)String.class);
        }
    }
    
    public static String upperCaseFirstLetter(final String s) {
        return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray()[24].callStatic((Class)PropertyUtil.class, (Object)s, (Object)new GeneratedClosure(PropertyUtil.class, PropertyUtil.class) {
            private static /* synthetic */ SoftReference $callSiteArray;
            
            public Object doCall(final String it) {
                return $getCallSiteArray()[0].call((Object)it);
            }
            
            public Object call(final String it) {
                return $getCallSiteArray()[1].callCurrent((GroovyObject)this, (Object)it);
            }
            
            public static /* synthetic */ void __$swapInit() {
                $getCallSiteArray();
                PropertyUtil$_upperCaseFirstLetter_closure1.$callSiteArray = null;
            }
            
            static {
                __$swapInit();
            }
            
            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                final String[] array = new String[2];
                $createCallSiteArray_1(array);
                return new CallSiteArray((Class)PropertyUtil$_upperCaseFirstLetter_closure1.class, array);
            }
            
            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray $createCallSiteArray;
                if (PropertyUtil$_upperCaseFirstLetter_closure1.$callSiteArray == null || ($createCallSiteArray = PropertyUtil$_upperCaseFirstLetter_closure1.$callSiteArray.get()) == null) {
                    $createCallSiteArray = $createCallSiteArray();
                    PropertyUtil$_upperCaseFirstLetter_closure1.$callSiteArray = new SoftReference($createCallSiteArray);
                }
                return $createCallSiteArray.array;
            }
        }), (Class)String.class);
    }
    
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PropertyUtil.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo $staticClassInfo = PropertyUtil.$staticClassInfo;
        if ($staticClassInfo == null) {
            $staticClassInfo = (PropertyUtil.$staticClassInfo = ClassInfo.getClassInfo((Class)this.getClass()));
        }
        return $staticClassInfo.getMetaClass();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        PropertyUtil.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        PropertyUtil.__timeStamp__239_neverHappen1354662885762 = 0L;
        PropertyUtil.__timeStamp = 1354662885762L;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[25];
        $createCallSiteArray_1(array);
        return new CallSiteArray((Class)PropertyUtil.class, array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (PropertyUtil.$callSiteArray == null || ($createCallSiteArray = PropertyUtil.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            PropertyUtil.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
