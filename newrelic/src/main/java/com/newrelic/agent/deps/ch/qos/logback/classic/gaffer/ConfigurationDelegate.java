// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.gaffer;

import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.util.CachingDateFormatter;
import com.newrelic.agent.deps.ch.qos.logback.classic.turbo.TurboFilter;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.GeneratedClosure;
import groovy.lang.Reference;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.org.slf4j.Logger;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import java.util.HashMap;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import java.util.Map;
import org.codehaus.groovy.runtime.GStringImpl;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusListener;
import org.codehaus.groovy.runtime.callsite.CallSite;
import com.newrelic.agent.deps.ch.qos.logback.core.util.Duration;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import com.newrelic.agent.deps.ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import java.lang.ref.SoftReference;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import java.util.List;
import groovy.lang.GroovyObject;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class ConfigurationDelegate extends ContextAwareBase implements GroovyObject
{
    private List<Appender> appenderList;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885781;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public ConfigurationDelegate() {
        $getCallSiteArray();
        this.appenderList = (List<Appender>)ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = this.$getStaticMetaClass();
    }
    
    public Object getDeclaredOrigin() {
        $getCallSiteArray();
        return this;
    }
    
    public void scan(final String scanPeriodStr) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final ReconfigureOnChangeFilter rocf = (ReconfigureOnChangeFilter)ScriptBytecodeAdapter.castToType($getCallSiteArray[0].callConstructor((Object)ReconfigureOnChangeFilter.class), (Class)ReconfigureOnChangeFilter.class);
        $getCallSiteArray[1].call((Object)rocf, $getCallSiteArray[2].callGroovyObjectGetProperty((Object)this));
        if (DefaultTypeTransformation.booleanUnbox((Object)scanPeriodStr)) {
            try {
                final Duration duration = (Duration)ScriptBytecodeAdapter.castToType($getCallSiteArray[3].call((Object)Duration.class, (Object)scanPeriodStr), (Class)Duration.class);
                $getCallSiteArray[4].call((Object)rocf, $getCallSiteArray[5].call((Object)duration));
                $getCallSiteArray[6].callCurrent((GroovyObject)this, $getCallSiteArray[7].call((Object)"Setting ReconfigureOnChangeFilter scanning period to ", (Object)duration));
            }
            catch (NumberFormatException nfe) {
                $getCallSiteArray[8].callCurrent((GroovyObject)this, $getCallSiteArray[9].call($getCallSiteArray[10].call((Object)"Error while converting [", $getCallSiteArray[11].callGroovyObjectGetProperty((Object)this)), (Object)"] to long"), (Object)nfe);
            }
        }
        $getCallSiteArray[12].call((Object)rocf);
        $getCallSiteArray[13].callCurrent((GroovyObject)this, (Object)"Adding ReconfigureOnChangeFilter as a turbo filter");
        $getCallSiteArray[14].call($getCallSiteArray[15].callGroovyObjectGetProperty((Object)this), (Object)rocf);
    }
    
    public void statusListener(final Class listenerClass) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final StatusListener statusListener = (StatusListener)ScriptBytecodeAdapter.castToType($getCallSiteArray[16].call((Object)listenerClass), (Class)StatusListener.class);
        $getCallSiteArray[17].call($getCallSiteArray[18].callGetProperty($getCallSiteArray[19].callGroovyObjectGetProperty((Object)this)), (Object)statusListener);
        if (statusListener instanceof ContextAware) {
            $getCallSiteArray[20].call((Object)ScriptBytecodeAdapter.castToType((Object)statusListener, (Class)ContextAware.class), $getCallSiteArray[21].callGroovyObjectGetProperty((Object)this));
        }
        if (statusListener instanceof LifeCycle) {
            $getCallSiteArray[22].call((Object)ScriptBytecodeAdapter.castToType((Object)statusListener, (Class)LifeCycle.class));
        }
        $getCallSiteArray[23].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { $getCallSiteArray[24].callGetProperty((Object)listenerClass) }, new String[] { "Added status listener of type [", "]" }));
    }
    
    public void conversionRule(final String conversionWord, final Class converterClass) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final String converterClassName = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[25].call((Object)converterClass), (Class)String.class);
        Map ruleRegistry = (Map)ScriptBytecodeAdapter.castToType($getCallSiteArray[26].call($getCallSiteArray[27].callGroovyObjectGetProperty((Object)this), $getCallSiteArray[28].callGetProperty((Object)CoreConstants.class)), (Class)Map.class);
        if (BytecodeInterface8.isOrigZ() && !ConfigurationDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)ruleRegistry, (Object)null)) {
                ruleRegistry = (Map)ScriptBytecodeAdapter.castToType($getCallSiteArray[33].callConstructor((Object)HashMap.class), (Class)Map.class);
                $getCallSiteArray[34].call($getCallSiteArray[35].callGroovyObjectGetProperty((Object)this), $getCallSiteArray[36].callGetProperty((Object)CoreConstants.class), (Object)ruleRegistry);
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)ruleRegistry, (Object)null)) {
            ruleRegistry = (Map)ScriptBytecodeAdapter.castToType($getCallSiteArray[29].callConstructor((Object)HashMap.class), (Class)Map.class);
            $getCallSiteArray[30].call($getCallSiteArray[31].callGroovyObjectGetProperty((Object)this), $getCallSiteArray[32].callGetProperty((Object)CoreConstants.class), (Object)ruleRegistry);
        }
        $getCallSiteArray[37].callCurrent((GroovyObject)this, $getCallSiteArray[38].call($getCallSiteArray[39].call($getCallSiteArray[40].call($getCallSiteArray[41].call((Object)"registering conversion word ", (Object)conversionWord), (Object)" with class ["), (Object)converterClassName), (Object)"]"));
        $getCallSiteArray[42].call((Object)ruleRegistry, (Object)conversionWord, (Object)converterClassName);
    }
    
    public void root(final Level level, final List<String> appenderNames) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (BytecodeInterface8.isOrigZ() && !ConfigurationDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)level, (Object)null)) {
                $getCallSiteArray[46].callCurrent((GroovyObject)this, (Object)"Root logger cannot be set to level null");
            }
            else {
                $getCallSiteArray[47].callCurrent((GroovyObject)this, $getCallSiteArray[48].callGetProperty((Object)Logger.class), (Object)level, (Object)appenderNames);
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)level, (Object)null)) {
            $getCallSiteArray[43].callCurrent((GroovyObject)this, (Object)"Root logger cannot be set to level null");
        }
        else {
            $getCallSiteArray[44].callCurrent((GroovyObject)this, $getCallSiteArray[45].callGetProperty((Object)Logger.class), (Object)level, (Object)appenderNames);
        }
    }
    
    public void logger(final String name, final Level level, final List<String> appenderNames, final Boolean additivity) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox((Object)name)) {
            final Reference logger = new Reference((Object)ScriptBytecodeAdapter.castToType($getCallSiteArray[49].call((Object)ScriptBytecodeAdapter.castToType($getCallSiteArray[50].callGroovyObjectGetProperty((Object)this), (Class)LoggerContext.class), (Object)name), (Class)com.newrelic.agent.deps.ch.qos.logback.classic.Logger.class));
            ScriptBytecodeAdapter.setProperty((Object)level, (Class)null, (Object)logger.get(), "level");
            if (DefaultTypeTransformation.booleanUnbox((Object)appenderNames)) {
                $getCallSiteArray[51].call((Object)appenderNames, (Object)new GeneratedClosure((Object)this, (Object)this) {
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;
                    
                    public Object doCall(final Object aName) {
                        final Reference aName2 = new Reference(aName);
                        final CallSite[] $getCallSiteArray = $getCallSiteArray();
                        final Appender appender = (Appender)ScriptBytecodeAdapter.castToType($getCallSiteArray[0].call($getCallSiteArray[1].callGroovyObjectGetProperty((Object)this), (Object)new GeneratedClosure((Object)this, this.getThisObject()) {
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;
                            
                            public Object doCall(final Object it) {
                                final CallSite[] $getCallSiteArray = $getCallSiteArray();
                                if (BytecodeInterface8.isOrigZ() && !ConfigurationDelegate$_logger_closure1_closure3.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
                                    return ScriptBytecodeAdapter.compareEqual($getCallSiteArray[1].callGetProperty(it), aName2.get());
                                }
                                return ScriptBytecodeAdapter.compareEqual($getCallSiteArray[0].callGetProperty(it), aName2.get());
                            }
                            
                            public Object getaName() {
                                $getCallSiteArray();
                                return aName2.get();
                            }
                            
                            public static /* synthetic */ void __$swapInit() {
                                $getCallSiteArray();
                                ConfigurationDelegate$_logger_closure1_closure3.$callSiteArray = null;
                            }
                            
                            static {
                                __$swapInit();
                            }
                            
                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                final String[] array = new String[2];
                                $createCallSiteArray_1(array);
                                return new CallSiteArray((Class)ConfigurationDelegate$_logger_closure1_closure3.class, array);
                            }
                            
                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray $createCallSiteArray;
                                if (ConfigurationDelegate$_logger_closure1_closure3.$callSiteArray == null || ($createCallSiteArray = ConfigurationDelegate$_logger_closure1_closure3.$callSiteArray.get()) == null) {
                                    $createCallSiteArray = $createCallSiteArray();
                                    ConfigurationDelegate$_logger_closure1_closure3.$callSiteArray = new SoftReference($createCallSiteArray);
                                }
                                return $createCallSiteArray.array;
                            }
                        }), (Class)Appender.class);
                        if (BytecodeInterface8.isOrigZ() && !ConfigurationDelegate$_logger_closure1.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
                            if (ScriptBytecodeAdapter.compareNotEqual((Object)appender, (Object)null)) {
                                return $getCallSiteArray[4].call(logger.get(), (Object)appender);
                            }
                            return $getCallSiteArray[5].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { aName2.get() }, new String[] { "Failed to find appender named [", "]" }));
                        }
                        else {
                            if (ScriptBytecodeAdapter.compareNotEqual((Object)appender, (Object)null)) {
                                return $getCallSiteArray[2].call(logger.get(), (Object)appender);
                            }
                            return $getCallSiteArray[3].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { aName2.get() }, new String[] { "Failed to find appender named [", "]" }));
                        }
                    }
                    
                    public com.newrelic.agent.deps.ch.qos.logback.classic.Logger getLogger() {
                        $getCallSiteArray();
                        return (com.newrelic.agent.deps.ch.qos.logback.classic.Logger)ScriptBytecodeAdapter.castToType(logger.get(), (Class)com.newrelic.agent.deps.ch.qos.logback.classic.Logger.class);
                    }
                    
                    public static /* synthetic */ void __$swapInit() {
                        $getCallSiteArray();
                        ConfigurationDelegate$_logger_closure1.$callSiteArray = null;
                    }
                    
                    static {
                        __$swapInit();
                    }
                    
                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        final String[] array = new String[6];
                        $createCallSiteArray_1(array);
                        return new CallSiteArray((Class)ConfigurationDelegate$_logger_closure1.class, array);
                    }
                    
                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray $createCallSiteArray;
                        if (ConfigurationDelegate$_logger_closure1.$callSiteArray == null || ($createCallSiteArray = ConfigurationDelegate$_logger_closure1.$callSiteArray.get()) == null) {
                            $createCallSiteArray = $createCallSiteArray();
                            ConfigurationDelegate$_logger_closure1.$callSiteArray = new SoftReference($createCallSiteArray);
                        }
                        return $createCallSiteArray.array;
                    }
                });
            }
            if (ScriptBytecodeAdapter.compareNotEqual((Object)additivity, (Object)null)) {
                ScriptBytecodeAdapter.setProperty((Object)additivity, (Class)null, (Object)logger.get(), "additive");
            }
        }
        else {
            $getCallSiteArray[52].callCurrent((GroovyObject)this, (Object)"No name attribute for logger");
        }
    }
    
    public void appender(final String name, final Class clazz, final Closure closure) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        $getCallSiteArray[53].callCurrent((GroovyObject)this, $getCallSiteArray[54].call($getCallSiteArray[55].call((Object)"About to instantiate appender of type [", $getCallSiteArray[56].callGetProperty((Object)clazz)), (Object)"]"));
        final Appender appender = (Appender)ScriptBytecodeAdapter.castToType($getCallSiteArray[57].call((Object)clazz), (Class)Appender.class);
        $getCallSiteArray[58].callCurrent((GroovyObject)this, $getCallSiteArray[59].call($getCallSiteArray[60].call((Object)"Naming appender as [", (Object)name), (Object)"]"));
        ScriptBytecodeAdapter.setProperty((Object)name, (Class)null, (Object)appender, "name");
        ScriptBytecodeAdapter.setProperty($getCallSiteArray[61].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)appender, "context");
        $getCallSiteArray[62].call((Object)this.appenderList, (Object)appender);
        Label_0499: {
            if (!BytecodeInterface8.isOrigZ() || ConfigurationDelegate.__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, (Object)null)) {
                    final AppenderDelegate ad = (AppenderDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[63].callConstructor((Object)AppenderDelegate.class, (Object)appender), (Class)AppenderDelegate.class);
                    $getCallSiteArray[64].callCurrent((GroovyObject)this, (Object)ad, (Object)appender);
                    ScriptBytecodeAdapter.setProperty($getCallSiteArray[65].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)ad, "context");
                    ScriptBytecodeAdapter.setGroovyObjectProperty((Object)ad, (Class)ConfigurationDelegate.class, (GroovyObject)closure, "delegate");
                    ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[66].callGetProperty((Object)Closure.class), (Class)ConfigurationDelegate.class, (GroovyObject)closure, "resolveStrategy");
                    $getCallSiteArray[67].call((Object)closure);
                }
                break Label_0499;
            }
            if (!ScriptBytecodeAdapter.compareNotEqual((Object)closure, (Object)null)) {
                break Label_0499;
            }
            final AppenderDelegate ad2 = (AppenderDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[68].callConstructor((Object)AppenderDelegate.class, (Object)appender), (Class)AppenderDelegate.class);
            $getCallSiteArray[69].callCurrent((GroovyObject)this, (Object)ad2, (Object)appender);
            ScriptBytecodeAdapter.setProperty($getCallSiteArray[70].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)ad2, "context");
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)ad2, (Class)ConfigurationDelegate.class, (GroovyObject)closure, "delegate");
            ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[71].callGetProperty((Object)Closure.class), (Class)ConfigurationDelegate.class, (GroovyObject)closure, "resolveStrategy");
            $getCallSiteArray[72].call((Object)closure);
            try {
                $getCallSiteArray[73].call((Object)appender);
            }
            catch (RuntimeException e) {
                $getCallSiteArray[74].callCurrent((GroovyObject)this, $getCallSiteArray[75].call($getCallSiteArray[76].call((Object)"Failed to start apppender named [", (Object)name), (Object)"]"), (Object)e);
            }
        }
    }
    
    private void copyContributions(final AppenderDelegate appenderDelegate, final Appender appender) {
        final Reference appenderDelegate2 = new Reference((Object)appenderDelegate);
        final Reference appender2 = new Reference((Object)appender);
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (((Appender)appender2.get()) instanceof ConfigurationContributor) {
            final ConfigurationContributor cc = (ConfigurationContributor)ScriptBytecodeAdapter.castToType((Object)appender2.get(), (Class)ConfigurationContributor.class);
            $getCallSiteArray[77].call($getCallSiteArray[78].call((Object)cc), (Object)new GeneratedClosure((Object)this, (Object)this) {
                private static /* synthetic */ SoftReference $callSiteArray;
                
                public Object doCall(final Object oldName, final Object newName) {
                    final CallSite[] $getCallSiteArray = $getCallSiteArray();
                    final Closure methodPointer = ScriptBytecodeAdapter.getMethodPointer(appender2.get(), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { oldName }, new String[] { "", "" }), (Class)String.class));
                    ScriptBytecodeAdapter.setProperty((Object)methodPointer, (Class)null, $getCallSiteArray[0].callGetProperty(appenderDelegate2.get()), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { newName }, new String[] { "", "" }), (Class)String.class));
                    return methodPointer;
                }
                
                public Object call(final Object oldName, final Object newName) {
                    return $getCallSiteArray()[1].callCurrent((GroovyObject)this, oldName, newName);
                }
                
                public AppenderDelegate getAppenderDelegate() {
                    $getCallSiteArray();
                    return (AppenderDelegate)ScriptBytecodeAdapter.castToType(appenderDelegate2.get(), (Class)AppenderDelegate.class);
                }
                
                public Appender getAppender() {
                    $getCallSiteArray();
                    return (Appender)ScriptBytecodeAdapter.castToType(appender2.get(), (Class)Appender.class);
                }
                
                public static /* synthetic */ void __$swapInit() {
                    $getCallSiteArray();
                    ConfigurationDelegate$_copyContributions_closure2.$callSiteArray = null;
                }
                
                static {
                    __$swapInit();
                }
                
                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    final String[] array = new String[2];
                    $createCallSiteArray_1(array);
                    return new CallSiteArray((Class)ConfigurationDelegate$_copyContributions_closure2.class, array);
                }
                
                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray $createCallSiteArray;
                    if (ConfigurationDelegate$_copyContributions_closure2.$callSiteArray == null || ($createCallSiteArray = ConfigurationDelegate$_copyContributions_closure2.$callSiteArray.get()) == null) {
                        $createCallSiteArray = $createCallSiteArray();
                        ConfigurationDelegate$_copyContributions_closure2.$callSiteArray = new SoftReference($createCallSiteArray);
                    }
                    return $createCallSiteArray.array;
                }
            });
        }
    }
    
    public void turboFilter(final Class clazz, final Closure closure) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        $getCallSiteArray[79].callCurrent((GroovyObject)this, $getCallSiteArray[80].call($getCallSiteArray[81].call((Object)"About to instantiate turboFilter of type [", $getCallSiteArray[82].callGetProperty((Object)clazz)), (Object)"]"));
        final TurboFilter turboFilter = (TurboFilter)ScriptBytecodeAdapter.castToType($getCallSiteArray[83].call((Object)clazz), (Class)TurboFilter.class);
        ScriptBytecodeAdapter.setProperty($getCallSiteArray[84].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)turboFilter, "context");
        if (BytecodeInterface8.isOrigZ() && !ConfigurationDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, (Object)null)) {
                final ComponentDelegate componentDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[89].callConstructor((Object)ComponentDelegate.class, (Object)turboFilter), (Class)ComponentDelegate.class);
                ScriptBytecodeAdapter.setProperty($getCallSiteArray[90].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)componentDelegate, "context");
                ScriptBytecodeAdapter.setGroovyObjectProperty((Object)componentDelegate, (Class)ConfigurationDelegate.class, (GroovyObject)closure, "delegate");
                ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[91].callGetProperty((Object)Closure.class), (Class)ConfigurationDelegate.class, (GroovyObject)closure, "resolveStrategy");
                $getCallSiteArray[92].call((Object)closure);
            }
        }
        else if (ScriptBytecodeAdapter.compareNotEqual((Object)closure, (Object)null)) {
            final ComponentDelegate componentDelegate2 = (ComponentDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[85].callConstructor((Object)ComponentDelegate.class, (Object)turboFilter), (Class)ComponentDelegate.class);
            ScriptBytecodeAdapter.setProperty($getCallSiteArray[86].callGroovyObjectGetProperty((Object)this), (Class)null, (Object)componentDelegate2, "context");
            ScriptBytecodeAdapter.setGroovyObjectProperty((Object)componentDelegate2, (Class)ConfigurationDelegate.class, (GroovyObject)closure, "delegate");
            ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[87].callGetProperty((Object)Closure.class), (Class)ConfigurationDelegate.class, (GroovyObject)closure, "resolveStrategy");
            $getCallSiteArray[88].call((Object)closure);
        }
        $getCallSiteArray[93].call((Object)turboFilter);
        $getCallSiteArray[94].callCurrent((GroovyObject)this, (Object)"Adding aforementioned turbo filter to context");
        $getCallSiteArray[95].call($getCallSiteArray[96].callGroovyObjectGetProperty((Object)this), (Object)turboFilter);
    }
    
    public String timestamp(final String datePattern, final long timeReference) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        long now = -1;
        if (BytecodeInterface8.isOrigL() && BytecodeInterface8.isOrigZ() && !ConfigurationDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)timeReference, (Object)(-1))) {
                $getCallSiteArray[102].callCurrent((GroovyObject)this, (Object)"Using current interpretation time, i.e. now, as time reference.");
                now = DefaultTypeTransformation.longUnbox($getCallSiteArray[103].call((Object)System.class));
            }
            else {
                now = timeReference;
                $getCallSiteArray[104].callCurrent((GroovyObject)this, $getCallSiteArray[105].call($getCallSiteArray[106].call((Object)"Using ", (Object)now), (Object)" as time reference."));
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)timeReference, (Object)(-1))) {
            $getCallSiteArray[97].callCurrent((GroovyObject)this, (Object)"Using current interpretation time, i.e. now, as time reference.");
            now = DefaultTypeTransformation.longUnbox($getCallSiteArray[98].call((Object)System.class));
        }
        else {
            now = timeReference;
            $getCallSiteArray[99].callCurrent((GroovyObject)this, $getCallSiteArray[100].call($getCallSiteArray[101].call((Object)"Using ", (Object)now), (Object)" as time reference."));
        }
        final CachingDateFormatter sdf = (CachingDateFormatter)ScriptBytecodeAdapter.castToType($getCallSiteArray[107].callConstructor((Object)CachingDateFormatter.class, (Object)datePattern), (Class)CachingDateFormatter.class);
        return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[108].call((Object)sdf, (Object)now), (Class)String.class);
    }
    
    public void scan() {
        $getCallSiteArray();
        if (!ConfigurationDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            this.scan(null);
        }
        else {
            this.scan(null);
        }
    }
    
    public void root(final Level level) {
        $getCallSiteArray();
        this.root(level, ScriptBytecodeAdapter.createList(new Object[0]));
    }
    
    public void logger(final String name, final Level level, final List<String> appenderNames) {
        $getCallSiteArray();
        this.logger(name, level, appenderNames, null);
    }
    
    public void logger(final String name, final Level level) {
        $getCallSiteArray();
        this.logger(name, level, ScriptBytecodeAdapter.createList(new Object[0]), null);
    }
    
    public void appender(final String name, final Class clazz) {
        $getCallSiteArray();
        this.appender(name, clazz, null);
    }
    
    public void turboFilter(final Class clazz) {
        $getCallSiteArray();
        this.turboFilter(clazz, null);
    }
    
    public String timestamp(final String datePattern) {
        $getCallSiteArray();
        if (!ConfigurationDelegate.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            return this.timestamp(datePattern, -1);
        }
        return this.timestamp(datePattern, -1);
    }
    
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConfigurationDelegate.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo $staticClassInfo = ConfigurationDelegate.$staticClassInfo;
        if ($staticClassInfo == null) {
            $staticClassInfo = (ConfigurationDelegate.$staticClassInfo = ClassInfo.getClassInfo((Class)this.getClass()));
        }
        return $staticClassInfo.getMetaClass();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        ConfigurationDelegate.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        ConfigurationDelegate.__timeStamp__239_neverHappen1354662885781 = 0L;
        ConfigurationDelegate.__timeStamp = 1354662885781L;
    }
    
    public List<Appender> getAppenderList() {
        return this.appenderList;
    }
    
    public void setAppenderList(final List<Appender> appenderList) {
        this.appenderList = appenderList;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[109];
        $createCallSiteArray_1(array);
        return new CallSiteArray((Class)ConfigurationDelegate.class, array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (ConfigurationDelegate.$callSiteArray == null || ($createCallSiteArray = ConfigurationDelegate.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            ConfigurationDelegate.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
