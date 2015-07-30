// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.gaffer;

import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import java.lang.ref.SoftReference;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import groovy.lang.GroovyObject;

public enum NestingType implements GroovyObject
{
    public static final NestingType NA;
    public static final NestingType SINGLE;
    public static final NestingType AS_COLLECTION;
    public static final NestingType MIN_VALUE;
    public static final NestingType MAX_VALUE;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    private transient /* synthetic */ MetaClass metaClass;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885817;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public NestingType(final String __str, final int __int) {
        $getCallSiteArray();
        super(__str, __int);
        this.metaClass = this.$getStaticMetaClass();
    }
    
    static {
        __$swapInit();
        NestingType.__timeStamp__239_neverHappen1354662885817 = 0L;
        NestingType.__timeStamp = 1354662885817L;
        NA = (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray()[13].callStatic((Class)NestingType.class, (Object)"NA", (Object)0), (Class)NestingType.class);
        SINGLE = (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray()[14].callStatic((Class)NestingType.class, (Object)"SINGLE", (Object)1), (Class)NestingType.class);
        AS_COLLECTION = (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray()[15].callStatic((Class)NestingType.class, (Object)"AS_COLLECTION", (Object)2), (Class)NestingType.class);
        MIN_VALUE = NestingType.NA;
        MAX_VALUE = NestingType.AS_COLLECTION;
    }
    
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != NestingType.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo $staticClassInfo = NestingType.$staticClassInfo;
        if ($staticClassInfo == null) {
            $staticClassInfo = (NestingType.$staticClassInfo = ClassInfo.getClassInfo((Class)this.getClass()));
        }
        return $staticClassInfo.getMetaClass();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        NestingType.$callSiteArray = null;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[16];
        $createCallSiteArray_1(array);
        return new CallSiteArray((Class)NestingType.class, array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (NestingType.$callSiteArray == null || ($createCallSiteArray = NestingType.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            NestingType.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
