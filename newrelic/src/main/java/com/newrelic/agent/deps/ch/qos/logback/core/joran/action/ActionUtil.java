// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.util.ContextUtil;
import java.util.Properties;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;

public class ActionUtil
{
    public static Scope stringToScope(final String scopeStr) {
        if (Scope.SYSTEM.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.SYSTEM;
        }
        if (Scope.CONTEXT.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.CONTEXT;
        }
        return Scope.LOCAL;
    }
    
    public static void setProperty(final InterpretationContext ic, final String key, final String value, final Scope scope) {
        switch (scope) {
            case LOCAL: {
                ic.addSubstitutionProperty(key, value);
                break;
            }
            case CONTEXT: {
                ic.getContext().putProperty(key, value);
                break;
            }
            case SYSTEM: {
                OptionHelper.setSystemProperty(ic, key, value);
                break;
            }
        }
    }
    
    public static void setProperties(final InterpretationContext ic, final Properties props, final Scope scope) {
        switch (scope) {
            case LOCAL: {
                ic.addSubstitutionProperties(props);
                break;
            }
            case CONTEXT: {
                final ContextUtil cu = new ContextUtil(ic.getContext());
                cu.addProperties(props);
                break;
            }
            case SYSTEM: {
                OptionHelper.setSystemProperties(ic, props);
                break;
            }
        }
    }
    
    public enum Scope
    {
        LOCAL, 
        CONTEXT, 
        SYSTEM;
    }
}
