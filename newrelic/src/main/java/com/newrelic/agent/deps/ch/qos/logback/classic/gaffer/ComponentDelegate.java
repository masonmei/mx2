// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.gaffer;

import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import java.lang.ref.SoftReference;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import java.util.List;
import groovy.lang.GroovyObject;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class ComponentDelegate extends ContextAwareBase implements GroovyObject
{
    private final Object component;
    private final List fieldsToCaccade;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885827;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public ComponentDelegate(final Object component) {
        $getCallSiteArray();
        this.fieldsToCaccade = ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = this.$getStaticMetaClass();
        this.component = component;
    }
    
    public String getLabel() {
        $getCallSiteArray();
        return "component";
    }
    
    public String getLabelFistLetterInUpperCase() {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (!ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[6].call($getCallSiteArray[7].call($getCallSiteArray[8].call((Object)this.getLabel(), (Object)0)), $getCallSiteArray[9].call((Object)this.getLabel(), (Object)1)), (Class)String.class);
        }
        return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[0].call($getCallSiteArray[1].call($getCallSiteArray[2].call($getCallSiteArray[3].callCurrent((GroovyObject)this), (Object)0)), $getCallSiteArray[4].call($getCallSiteArray[5].callCurrent((GroovyObject)this), (Object)1)), (Class)String.class);
    }
    
    public void methodMissing(final String name, final Object args) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final NestingType nestingType = (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray[10].call((Object)PropertyUtil.class, this.component, (Object)name), (Class)NestingType.class);
        if (BytecodeInterface8.isOrigZ() && !ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)nestingType, $getCallSiteArray[17].callGetProperty((Object)NestingType.class))) {
                $getCallSiteArray[18].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { this.getLabelFistLetterInUpperCase(), this.getComponentName(), $getCallSiteArray[19].callGetProperty($getCallSiteArray[20].call(this.component)), name }, new String[] { "", " ", " of type [", "] has no appplicable [", "] property " }));
                return;
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)nestingType, $getCallSiteArray[11].callGetProperty((Object)NestingType.class))) {
            $getCallSiteArray[12].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { $getCallSiteArray[13].callCurrent((GroovyObject)this), $getCallSiteArray[14].callCurrent((GroovyObject)this), $getCallSiteArray[15].callGetProperty($getCallSiteArray[16].call(this.component)), name }, new String[] { "", " ", " of type [", "] has no appplicable [", "] property " }));
            return;
        }
        String subComponentName = null;
        Class clazz = null;
        Closure closure = null;
        final Object callCurrent = $getCallSiteArray[21].callCurrent((GroovyObject)this, args);
        subComponentName = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[22].call(callCurrent, (Object)0), (Class)String.class);
        clazz = (Class)ScriptBytecodeAdapter.castToType($getCallSiteArray[23].call(callCurrent, (Object)1), (Class)Class.class);
        closure = (Closure)ScriptBytecodeAdapter.castToType($getCallSiteArray[24].call(callCurrent, (Object)2), (Class)Closure.class);
        if (BytecodeInterface8.isOrigZ() && !ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual((Object)clazz, (Object)null)) {
                final Object subComponent = $getCallSiteArray[42].call((Object)clazz);
                if (DefaultTypeTransformation.booleanUnbox((Object)subComponentName) && DefaultTypeTransformation.booleanUnbox($getCallSiteArray[43].call(subComponent, (Object)name))) {
                    ScriptBytecodeAdapter.setProperty((Object)subComponentName, (Class)null, subComponent, "name");
                }
                if (subComponent instanceof ContextAware) {
                    ScriptBytecodeAdapter.setProperty($getCallSiteArray[44].callGroovyObjectGetProperty((Object)this), (Class)null, subComponent, "context");
                }
                if (DefaultTypeTransformation.booleanUnbox((Object)closure)) {
                    final ComponentDelegate subDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[45].callConstructor((Object)ComponentDelegate.class, subComponent), (Class)ComponentDelegate.class);
                    $getCallSiteArray[46].callCurrent((GroovyObject)this, (Object)subDelegate);
                    ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[47].callGroovyObjectGetProperty((Object)this), (Class)ComponentDelegate.class, (GroovyObject)subDelegate, "context");
                    $getCallSiteArray[48].callCurrent((GroovyObject)this, subComponent);
                    ScriptBytecodeAdapter.setGroovyObjectProperty((Object)subDelegate, (Class)ComponentDelegate.class, (GroovyObject)closure, "delegate");
                    ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[49].callGetProperty((Object)Closure.class), (Class)ComponentDelegate.class, (GroovyObject)closure, "resolveStrategy");
                    $getCallSiteArray[50].call((Object)closure);
                }
                if (subComponent instanceof LifeCycle && DefaultTypeTransformation.booleanUnbox($getCallSiteArray[51].call((Object)NoAutoStartUtil.class, subComponent))) {
                    $getCallSiteArray[52].call(subComponent);
                }
                $getCallSiteArray[53].call((Object)PropertyUtil.class, (Object)nestingType, this.component, subComponent, (Object)name);
            }
            else {
                $getCallSiteArray[54].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { name, this.getLabel(), this.getComponentName(), $getCallSiteArray[55].callGetProperty($getCallSiteArray[56].call(this.component)) }, new String[] { "No 'class' argument specified for [", "] in ", " ", " of type [", "]" }));
            }
        }
        else if (ScriptBytecodeAdapter.compareNotEqual((Object)clazz, (Object)null)) {
            final Object subComponent2 = $getCallSiteArray[25].call((Object)clazz);
            if (DefaultTypeTransformation.booleanUnbox((Object)subComponentName) && DefaultTypeTransformation.booleanUnbox($getCallSiteArray[26].call(subComponent2, (Object)name))) {
                ScriptBytecodeAdapter.setProperty((Object)subComponentName, (Class)null, subComponent2, "name");
            }
            if (subComponent2 instanceof ContextAware) {
                ScriptBytecodeAdapter.setProperty($getCallSiteArray[27].callGroovyObjectGetProperty((Object)this), (Class)null, subComponent2, "context");
            }
            if (DefaultTypeTransformation.booleanUnbox((Object)closure)) {
                final ComponentDelegate subDelegate2 = (ComponentDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[28].callConstructor((Object)ComponentDelegate.class, subComponent2), (Class)ComponentDelegate.class);
                $getCallSiteArray[29].callCurrent((GroovyObject)this, (Object)subDelegate2);
                ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[30].callGroovyObjectGetProperty((Object)this), (Class)ComponentDelegate.class, (GroovyObject)subDelegate2, "context");
                $getCallSiteArray[31].callCurrent((GroovyObject)this, subComponent2);
                ScriptBytecodeAdapter.setGroovyObjectProperty((Object)subDelegate2, (Class)ComponentDelegate.class, (GroovyObject)closure, "delegate");
                ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[32].callGetProperty((Object)Closure.class), (Class)ComponentDelegate.class, (GroovyObject)closure, "resolveStrategy");
                $getCallSiteArray[33].call((Object)closure);
            }
            if (subComponent2 instanceof LifeCycle && DefaultTypeTransformation.booleanUnbox($getCallSiteArray[34].call((Object)NoAutoStartUtil.class, subComponent2))) {
                $getCallSiteArray[35].call(subComponent2);
            }
            $getCallSiteArray[36].call((Object)PropertyUtil.class, (Object)nestingType, this.component, subComponent2, (Object)name);
        }
        else {
            $getCallSiteArray[37].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { name, $getCallSiteArray[38].callCurrent((GroovyObject)this), $getCallSiteArray[39].callCurrent((GroovyObject)this), $getCallSiteArray[40].callGetProperty($getCallSiteArray[41].call(this.component)) }, new String[] { "No 'class' argument specified for [", "] in ", " ", " of type [", "]" }));
        }
    }
    
    public void cascadeFields(final ComponentDelegate subDelegate) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        String k = null;
        final Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType($getCallSiteArray[57].call((Object)this.fieldsToCaccade), (Class)Iterator.class);
        while (iterator.hasNext()) {
            k = (String)ScriptBytecodeAdapter.castToType(iterator.next(), (Class)String.class);
            ScriptBytecodeAdapter.setProperty(ScriptBytecodeAdapter.getGroovyObjectProperty((Class)ComponentDelegate.class, (GroovyObject)this, (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { k }, new String[] { "", "" }), (Class)String.class)), (Class)null, $getCallSiteArray[58].callGroovyObjectGetProperty((Object)subDelegate), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { k }, new String[] { "", "" }), (Class)String.class));
        }
    }
    
    public void injectParent(final Object subComponent) {
        if (DefaultTypeTransformation.booleanUnbox($getCallSiteArray()[59].call(subComponent, (Object)"parent"))) {
            ScriptBytecodeAdapter.setProperty(this.component, (Class)null, subComponent, "parent");
        }
    }
    
    public void propertyMissing(final String name, final Object value) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final NestingType nestingType = (NestingType)ScriptBytecodeAdapter.castToType($getCallSiteArray[60].call((Object)PropertyUtil.class, this.component, (Object)name), (Class)NestingType.class);
        if (BytecodeInterface8.isOrigZ() && !ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)nestingType, $getCallSiteArray[67].callGetProperty((Object)NestingType.class))) {
                $getCallSiteArray[68].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { this.getLabelFistLetterInUpperCase(), this.getComponentName(), $getCallSiteArray[69].callGetProperty($getCallSiteArray[70].call(this.component)), name }, new String[] { "", " ", " of type [", "] has no appplicable [", "] property " }));
                return;
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)nestingType, $getCallSiteArray[61].callGetProperty((Object)NestingType.class))) {
            $getCallSiteArray[62].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { $getCallSiteArray[63].callCurrent((GroovyObject)this), $getCallSiteArray[64].callCurrent((GroovyObject)this), $getCallSiteArray[65].callGetProperty($getCallSiteArray[66].call(this.component)), name }, new String[] { "", " ", " of type [", "] has no appplicable [", "] property " }));
            return;
        }
        $getCallSiteArray[71].call((Object)PropertyUtil.class, (Object)nestingType, this.component, value, (Object)name);
    }
    
    public Object analyzeArgs(Object... args) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        String name = null;
        Class clazz = null;
        Closure closure = null;
        if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareGreaterThan($getCallSiteArray[74].call((Object)args), (Object)3)) {
                $getCallSiteArray[75].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { args }, new String[] { "At most 3 arguments allowed but you passed ", "" }));
                return ScriptBytecodeAdapter.createList(new Object[] { name, clazz, closure });
            }
        }
        else if (ScriptBytecodeAdapter.compareGreaterThan($getCallSiteArray[72].call((Object)args), (Object)3)) {
            $getCallSiteArray[73].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { args }, new String[] { "At most 3 arguments allowed but you passed ", "" }));
            return ScriptBytecodeAdapter.createList(new Object[] { name, clazz, closure });
        }
        if (!ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (BytecodeInterface8.objectArrayGet(args, -1) instanceof Closure) {
                closure = (Closure)ScriptBytecodeAdapter.castToType(BytecodeInterface8.objectArrayGet(args, -1), (Class)Closure.class);
                args = (Object[])ScriptBytecodeAdapter.castToType($getCallSiteArray[80].call((Object)args, BytecodeInterface8.objectArrayGet(args, -1)), (Class)Object[].class);
            }
        }
        else if ($getCallSiteArray[76].call((Object)args, (Object)(-1)) instanceof Closure) {
            closure = (Closure)ScriptBytecodeAdapter.castToType($getCallSiteArray[77].call((Object)args, (Object)(-1)), (Class)Closure.class);
            args = (Object[])ScriptBytecodeAdapter.castToType($getCallSiteArray[78].call((Object)args, $getCallSiteArray[79].call((Object)args, (Object)(-1))), (Class)Object[].class);
        }
        if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual($getCallSiteArray[84].call((Object)args), (Object)1)) {
                clazz = (Class)ScriptBytecodeAdapter.castToType($getCallSiteArray[85].callCurrent((GroovyObject)this, BytecodeInterface8.objectArrayGet(args, 0)), (Class)Class.class);
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual($getCallSiteArray[81].call((Object)args), (Object)1)) {
            clazz = (Class)ScriptBytecodeAdapter.castToType($getCallSiteArray[82].callCurrent((GroovyObject)this, $getCallSiteArray[83].call((Object)args, (Object)0)), (Class)Class.class);
        }
        if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !ComponentDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual($getCallSiteArray[91].call((Object)args), (Object)2)) {
                name = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[92].callCurrent((GroovyObject)this, BytecodeInterface8.objectArrayGet(args, 0)), (Class)String.class);
                clazz = (Class)ScriptBytecodeAdapter.castToType($getCallSiteArray[93].callCurrent((GroovyObject)this, BytecodeInterface8.objectArrayGet(args, 1)), (Class)Class.class);
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual($getCallSiteArray[86].call((Object)args), (Object)2)) {
            name = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[87].callCurrent((GroovyObject)this, $getCallSiteArray[88].call((Object)args, (Object)0)), (Class)String.class);
            clazz = (Class)ScriptBytecodeAdapter.castToType($getCallSiteArray[89].callCurrent((GroovyObject)this, $getCallSiteArray[90].call((Object)args, (Object)1)), (Class)Class.class);
        }
        return ScriptBytecodeAdapter.createList(new Object[] { name, clazz, closure });
    }
    
    public Class parseClassArgument(final Object arg) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (arg instanceof Class) {
            return (Class)ScriptBytecodeAdapter.castToType(arg, (Class)Class.class);
        }
        if (arg instanceof String) {
            return (Class)ScriptBytecodeAdapter.castToType($getCallSiteArray[94].call((Object)Class.class, arg), (Class)Class.class);
        }
        $getCallSiteArray[95].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { $getCallSiteArray[96].callGetProperty($getCallSiteArray[97].call(arg)) }, new String[] { "Unexpected argument type ", "" }));
        return (Class)ScriptBytecodeAdapter.castToType((Object)null, (Class)Class.class);
    }
    
    public String parseNameArgument(final Object arg) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (arg instanceof String) {
            return (String)ScriptBytecodeAdapter.castToType(arg, (Class)String.class);
        }
        $getCallSiteArray[98].callCurrent((GroovyObject)this, (Object)"With 2 or 3 arguments, the first argument must be the component name, i.e of type string");
        return (String)ScriptBytecodeAdapter.castToType((Object)null, (Class)String.class);
    }
    
    public String getComponentName() {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox($getCallSiteArray[99].call(this.component, (Object)"name"))) {
            return (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { $getCallSiteArray[100].callGetProperty(this.component) }, new String[] { "[", "]" }), (Class)String.class);
        }
        return "";
    }
    
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ComponentDelegate.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo $staticClassInfo = ComponentDelegate.$staticClassInfo;
        if ($staticClassInfo == null) {
            $staticClassInfo = (ComponentDelegate.$staticClassInfo = ClassInfo.getClassInfo((Class)this.getClass()));
        }
        return $staticClassInfo.getMetaClass();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        ComponentDelegate.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        ComponentDelegate.__timeStamp__239_neverHappen1354662885827 = 0L;
        ComponentDelegate.__timeStamp = 1354662885827L;
    }
    
    public final Object getComponent() {
        return this.component;
    }
    
    public final List getFieldsToCaccade() {
        return this.fieldsToCaccade;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[101];
        $createCallSiteArray_1(array);
        return new CallSiteArray((Class)ComponentDelegate.class, array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (ComponentDelegate.$callSiteArray == null || ($createCallSiteArray = ComponentDelegate.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            ComponentDelegate.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
