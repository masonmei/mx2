// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThrowableProxy implements IThrowableProxy
{
    private Throwable throwable;
    private String className;
    private String message;
    StackTraceElementProxy[] stackTraceElementProxyArray;
    int commonFrames;
    private ThrowableProxy cause;
    private ThrowableProxy[] suppressed;
    private transient PackagingDataCalculator packagingDataCalculator;
    private boolean calculatedPackageData;
    private static final Method GET_SUPPRESSED_METHOD;
    private static final ThrowableProxy[] NO_SUPPRESSED;
    
    public ThrowableProxy(final Throwable throwable) {
        this.suppressed = ThrowableProxy.NO_SUPPRESSED;
        this.calculatedPackageData = false;
        this.throwable = throwable;
        this.className = throwable.getClass().getName();
        this.message = throwable.getMessage();
        this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());
        final Throwable nested = throwable.getCause();
        if (nested != null) {
            this.cause = new ThrowableProxy(nested);
            this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(), this.stackTraceElementProxyArray);
        }
        if (ThrowableProxy.GET_SUPPRESSED_METHOD != null) {
            try {
                final Object obj = ThrowableProxy.GET_SUPPRESSED_METHOD.invoke(throwable, new Object[0]);
                if (obj instanceof Throwable[]) {
                    final Throwable[] throwableSuppressed = (Throwable[])obj;
                    if (throwableSuppressed.length > 0) {
                        this.suppressed = new ThrowableProxy[throwableSuppressed.length];
                        for (int i = 0; i < throwableSuppressed.length; ++i) {
                            this.suppressed[i] = new ThrowableProxy(throwableSuppressed[i]);
                            this.suppressed[i].commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(throwableSuppressed[i].getStackTrace(), this.stackTraceElementProxyArray);
                        }
                    }
                }
            }
            catch (IllegalAccessException e) {}
            catch (InvocationTargetException ex) {}
        }
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return this.stackTraceElementProxyArray;
    }
    
    public int getCommonFrames() {
        return this.commonFrames;
    }
    
    public IThrowableProxy getCause() {
        return this.cause;
    }
    
    public IThrowableProxy[] getSuppressed() {
        return this.suppressed;
    }
    
    public PackagingDataCalculator getPackagingDataCalculator() {
        if (this.throwable != null && this.packagingDataCalculator == null) {
            this.packagingDataCalculator = new PackagingDataCalculator();
        }
        return this.packagingDataCalculator;
    }
    
    public void calculatePackagingData() {
        if (this.calculatedPackageData) {
            return;
        }
        final PackagingDataCalculator pdc = this.getPackagingDataCalculator();
        if (pdc != null) {
            this.calculatedPackageData = true;
            pdc.calculate(this);
        }
    }
    
    public void fullDump() {
        final StringBuilder builder = new StringBuilder();
        for (final StackTraceElementProxy step : this.stackTraceElementProxyArray) {
            final String string = step.toString();
            builder.append('\t').append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
            builder.append(CoreConstants.LINE_SEPARATOR);
        }
        System.out.println(builder.toString());
    }
    
    static {
        Method method = null;
        try {
            method = Throwable.class.getMethod("getSuppressed", (Class<?>[])new Class[0]);
        }
        catch (NoSuchMethodException ex) {}
        GET_SUPPRESSED_METHOD = method;
        NO_SUPPRESSED = new ThrowableProxy[0];
    }
}
