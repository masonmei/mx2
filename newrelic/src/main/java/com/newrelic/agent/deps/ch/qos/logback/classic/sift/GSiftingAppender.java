// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.Filter;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.core.helpers.NOPAppender;
import org.codehaus.groovy.runtime.GStringImpl;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import java.util.Iterator;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import java.util.Map;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.AppenderTrackerImpl;
import java.lang.ref.SoftReference;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import groovy.lang.Closure;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.Discriminator;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.AppenderTracker;
import groovy.lang.GroovyObject;
import com.newrelic.agent.deps.ch.qos.logback.classic.gaffer.ConfigurationContributor;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;

public class GSiftingAppender extends AppenderBase implements ConfigurationContributor, GroovyObject
{
    protected AppenderTracker<ILoggingEvent> appenderTracker;
    private Discriminator<ILoggingEvent> discriminator;
    private Closure builderClosure;
    private int nopaWarningCount;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    public static /* synthetic */ long __timeStamp;
    public static /* synthetic */ long __timeStamp__239_neverHappen1354662885844;
    private static /* synthetic */ SoftReference $callSiteArray;
    
    public GSiftingAppender() {
        this.appenderTracker = (AppenderTracker<ILoggingEvent>)ScriptBytecodeAdapter.castToType($getCallSiteArray()[0].callConstructor((Object)AppenderTrackerImpl.class), (Class)AppenderTracker.class);
        this.nopaWarningCount = 0;
        this.metaClass = this.$getStaticMetaClass();
    }
    
    public Map<String, String> getMappings() {
        $getCallSiteArray();
        return (Map<String, String>)ScriptBytecodeAdapter.createMap(new Object[] { "sift", "sift" });
    }
    
    public void start() {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        int errors = 0;
        if (BytecodeInterface8.isOrigZ() && !GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)this.discriminator, (Object)null)) {
                $getCallSiteArray[3].callCurrent((GroovyObject)this, (Object)"Missing discriminator. Aborting");
                errors++;
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)this.discriminator, (Object)null)) {
            $getCallSiteArray[1].callCurrent((GroovyObject)this, (Object)"Missing discriminator. Aborting");
            errors = DefaultTypeTransformation.intUnbox($getCallSiteArray[2].call((Object)errors));
        }
        if (!DefaultTypeTransformation.booleanUnbox($getCallSiteArray[4].callSafe((Object)this.discriminator))) {
            $getCallSiteArray[5].callCurrent((GroovyObject)this, (Object)"Discriminator has not started successfully. Aborting");
            errors = DefaultTypeTransformation.intUnbox($getCallSiteArray[6].call((Object)errors));
        }
        if (BytecodeInterface8.isOrigZ() && !GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)this.builderClosure, (Object)null)) {
                $getCallSiteArray[9].callCurrent((GroovyObject)this, (Object)"Missing builder closure. Aborting");
                errors++;
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)this.builderClosure, (Object)null)) {
            $getCallSiteArray[7].callCurrent((GroovyObject)this, (Object)"Missing builder closure. Aborting");
            errors = DefaultTypeTransformation.intUnbox($getCallSiteArray[8].call((Object)errors));
        }
        if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (errors == 0) {
                ScriptBytecodeAdapter.invokeMethodOnSuper0((Class)AppenderBase.class, (GroovyObject)this, "start");
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)errors, (Object)0)) {
            ScriptBytecodeAdapter.invokeMethodOnSuper0((Class)AppenderBase.class, (GroovyObject)this, "start");
        }
    }
    
    public void stop() {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        Appender appender = null;
        final Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType($getCallSiteArray[10].call($getCallSiteArray[11].call((Object)this.appenderTracker)), (Class)Iterator.class);
        while (iterator.hasNext()) {
            appender = (Appender)ScriptBytecodeAdapter.castToType(iterator.next(), (Class)Appender.class);
            $getCallSiteArray[12].call((Object)appender);
        }
    }
    
    protected long getTimestamp(final ILoggingEvent event) {
        return DefaultTypeTransformation.longUnbox($getCallSiteArray()[13].call((Object)event));
    }
    
    public Appender buildAppender(final String value) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        String key = null;
        if (!GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            key = this.getDiscriminatorKey();
        }
        else {
            key = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[14].callCurrent((GroovyObject)this), (Class)String.class);
        }
        ZSiftingDelegate zd = null;
        if (!GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            zd = (ZSiftingDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[17].callConstructor((Object)ZSiftingDelegate.class, (Object)this.getDiscriminatorKey(), (Object)value), (Class)ZSiftingDelegate.class);
        }
        else {
            zd = (ZSiftingDelegate)ScriptBytecodeAdapter.castToType($getCallSiteArray[15].callConstructor((Object)ZSiftingDelegate.class, $getCallSiteArray[16].callCurrent((GroovyObject)this), (Object)value), (Class)ZSiftingDelegate.class);
        }
        ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[18].callGroovyObjectGetProperty((Object)this), (Class)GSiftingAppender.class, (GroovyObject)zd, "context");
        ScriptBytecodeAdapter.setProperty((Object)value, (Class)null, $getCallSiteArray[19].callGroovyObjectGetProperty((Object)zd), (String)ScriptBytecodeAdapter.castToType((Object)new GStringImpl(new Object[] { key }, new String[] { "", "" }), (Class)String.class));
        ScriptBytecodeAdapter.setGroovyObjectProperty((Object)zd, (Class)GSiftingAppender.class, (GroovyObject)this.builderClosure, "delegate");
        ScriptBytecodeAdapter.setGroovyObjectProperty($getCallSiteArray[20].callGetProperty((Object)Closure.class), (Class)GSiftingAppender.class, (GroovyObject)this.builderClosure, "resolveStrategy");
        final Appender a = (Appender)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.invokeClosure((Object)this.builderClosure, new Object[0]), (Class)Appender.class);
        return a;
    }
    
    public void append(final Object object) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final ILoggingEvent event = (ILoggingEvent)ScriptBytecodeAdapter.castToType(object, (Class)ILoggingEvent.class);
        if (!DefaultTypeTransformation.booleanUnbox($getCallSiteArray[21].callCurrent((GroovyObject)this))) {
            return;
        }
        final String discriminatingValue = (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[22].call((Object)this.discriminator, (Object)event), (Class)String.class);
        final long timestamp = DefaultTypeTransformation.longUnbox($getCallSiteArray[23].callCurrent((GroovyObject)this, (Object)event));
        Appender appender = (Appender)ScriptBytecodeAdapter.castToType($getCallSiteArray[24].call((Object)this.appenderTracker, (Object)discriminatingValue, (Object)timestamp), (Class)Appender.class);
        if (BytecodeInterface8.isOrigZ() && !GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual((Object)appender, (Object)null)) {
                try {
                    appender = this.buildAppender(discriminatingValue);
                    if (ScriptBytecodeAdapter.compareEqual((Object)appender, (Object)null)) {
                        appender = this.buildNOPAppender(discriminatingValue);
                    }
                    $getCallSiteArray[31].call((Object)this.appenderTracker, (Object)discriminatingValue, (Object)appender, (Object)timestamp);
                }
                catch (Throwable e) {
                    $getCallSiteArray[32].callCurrent((GroovyObject)this, $getCallSiteArray[33].call($getCallSiteArray[34].call((Object)"Failed to build appender for [", (Object)discriminatingValue), (Object)"]"), (Object)e);
                    return;
                }
            }
        }
        else if (ScriptBytecodeAdapter.compareEqual((Object)appender, (Object)null)) {
            try {
                appender = (Appender)ScriptBytecodeAdapter.castToType($getCallSiteArray[25].callCurrent((GroovyObject)this, (Object)discriminatingValue), (Class)Appender.class);
                if (ScriptBytecodeAdapter.compareEqual((Object)appender, (Object)null)) {
                    appender = (Appender)ScriptBytecodeAdapter.castToType($getCallSiteArray[26].callCurrent((GroovyObject)this, (Object)discriminatingValue), (Class)Appender.class);
                }
                $getCallSiteArray[27].call((Object)this.appenderTracker, (Object)discriminatingValue, (Object)appender, (Object)timestamp);
            }
            catch (Throwable e2) {
                $getCallSiteArray[28].callCurrent((GroovyObject)this, $getCallSiteArray[29].call($getCallSiteArray[30].call((Object)"Failed to build appender for [", (Object)discriminatingValue), (Object)"]"), (Object)e2);
                return;
            }
        }
        $getCallSiteArray[35].call((Object)this.appenderTracker, (Object)timestamp);
        $getCallSiteArray[36].call((Object)appender, (Object)event);
    }
    
    public NOPAppender<ILoggingEvent> buildNOPAppender(final String discriminatingValue) {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (BytecodeInterface8.isOrigInt() && BytecodeInterface8.isOrigZ() && !GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareLessThan((Object)this.nopaWarningCount, $getCallSiteArray[42].callGetProperty((Object)CoreConstants.class))) {
                this.nopaWarningCount++;
                $getCallSiteArray[43].callCurrent((GroovyObject)this, $getCallSiteArray[44].call($getCallSiteArray[45].call((Object)"Failed to build an appender for discriminating value [", (Object)discriminatingValue), (Object)"]"));
            }
        }
        else if (ScriptBytecodeAdapter.compareLessThan((Object)this.nopaWarningCount, $getCallSiteArray[37].callGetProperty((Object)CoreConstants.class))) {
            this.nopaWarningCount = DefaultTypeTransformation.intUnbox($getCallSiteArray[38].call((Object)this.nopaWarningCount));
            $getCallSiteArray[39].callCurrent((GroovyObject)this, $getCallSiteArray[40].call($getCallSiteArray[41].call((Object)"Failed to build an appender for discriminating value [", (Object)discriminatingValue), (Object)"]"));
        }
        final NOPAppender nopa = (NOPAppender)ScriptBytecodeAdapter.castToType($getCallSiteArray[46].callConstructor((Object)NOPAppender.class), (Class)NOPAppender.class);
        $getCallSiteArray[47].call((Object)nopa, $getCallSiteArray[48].callGroovyObjectGetProperty((Object)this));
        $getCallSiteArray[49].call((Object)nopa);
        return (NOPAppender<ILoggingEvent>)nopa;
    }
    
    public void build() {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        final int r = DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.invokeClosure((Object)this.builderClosure, new Object[0]));
        $getCallSiteArray[50].callCurrent((GroovyObject)this, (Object)new GStringImpl(new Object[] { r }, new String[] { "r=", "" }));
    }
    
    public void sift(final Closure clo) {
        $getCallSiteArray();
        this.builderClosure = clo;
    }
    
    public AppenderTracker getAppenderTracker() {
        $getCallSiteArray();
        return this.appenderTracker;
    }
    
    public String getDiscriminatorKey() {
        final CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (BytecodeInterface8.isOrigZ() && !GSiftingAppender.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual((Object)this.discriminator, (Object)null)) {
                return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[52].call((Object)this.discriminator), (Class)String.class);
            }
            return (String)ScriptBytecodeAdapter.castToType((Object)null, (Class)String.class);
        }
        else {
            if (ScriptBytecodeAdapter.compareNotEqual((Object)this.discriminator, (Object)null)) {
                return (String)ScriptBytecodeAdapter.castToType($getCallSiteArray[51].call((Object)this.discriminator), (Class)String.class);
            }
            return (String)ScriptBytecodeAdapter.castToType((Object)null, (Class)String.class);
        }
    }
    
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GSiftingAppender.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)this);
        }
        ClassInfo $staticClassInfo = GSiftingAppender.$staticClassInfo;
        if ($staticClassInfo == null) {
            $staticClassInfo = (GSiftingAppender.$staticClassInfo = ClassInfo.getClassInfo((Class)this.getClass()));
        }
        return $staticClassInfo.getMetaClass();
    }
    
    public static /* synthetic */ void __$swapInit() {
        $getCallSiteArray();
        GSiftingAppender.$callSiteArray = null;
    }
    
    static {
        __$swapInit();
        GSiftingAppender.__timeStamp__239_neverHappen1354662885844 = 0L;
        GSiftingAppender.__timeStamp = 1354662885844L;
    }
    
    public Discriminator<ILoggingEvent> getDiscriminator() {
        return this.discriminator;
    }
    
    public void setDiscriminator(final Discriminator<ILoggingEvent> discriminator) {
        this.discriminator = discriminator;
    }
    
    public Closure getBuilderClosure() {
        return this.builderClosure;
    }
    
    public void setBuilderClosure(final Closure builderClosure) {
        this.builderClosure = builderClosure;
    }
    
    public int getNopaWarningCount() {
        return this.nopaWarningCount;
    }
    
    public void setNopaWarningCount(final int nopaWarningCount) {
        this.nopaWarningCount = nopaWarningCount;
    }
    
    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        final String[] array = new String[53];
        $createCallSiteArray_1(array);
        return new CallSiteArray((Class)GSiftingAppender.class, array);
    }
    
    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray $createCallSiteArray;
        if (GSiftingAppender.$callSiteArray == null || ($createCallSiteArray = GSiftingAppender.$callSiteArray.get()) == null) {
            $createCallSiteArray = $createCallSiteArray();
            GSiftingAppender.$callSiteArray = new SoftReference($createCallSiteArray);
        }
        return $createCallSiteArray.array;
    }
}
