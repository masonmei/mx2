// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran.action;

import javax.naming.Context;
import javax.naming.NamingException;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.JNDIUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.ActionUtil;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class InsertFromJNDIAction extends Action
{
    public static final String ENV_ENTRY_NAME_ATTR = "env-entry-name";
    public static final String AS_ATTR = "as";
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) {
        int errorCount = 0;
        final String envEntryName = ec.subst(attributes.getValue("env-entry-name"));
        final String asKey = ec.subst(attributes.getValue("as"));
        final String scopeStr = attributes.getValue("scope");
        final ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        if (OptionHelper.isEmpty(envEntryName)) {
            final String lineColStr = this.getLineColStr(ec);
            this.addError("[env-entry-name] missing, around " + lineColStr);
            ++errorCount;
        }
        if (OptionHelper.isEmpty(asKey)) {
            final String lineColStr = this.getLineColStr(ec);
            this.addError("[as] missing, around " + lineColStr);
            ++errorCount;
        }
        if (errorCount != 0) {
            return;
        }
        try {
            final Context ctx = JNDIUtil.getInitialContext();
            final String envEntryValue = JNDIUtil.lookup(ctx, envEntryName);
            if (OptionHelper.isEmpty(envEntryValue)) {
                this.addError("[" + envEntryName + "] has null or empty value");
            }
            else {
                this.addInfo("Setting variable [" + asKey + "] to [" + envEntryValue + "] in [" + scope + "] scope");
                ActionUtil.setProperty(ec, asKey, envEntryValue, scope);
            }
        }
        catch (NamingException e) {
            this.addError("Failed to lookup JNDI env-entry [" + envEntryName + "]");
        }
    }
    
    public void end(final InterpretationContext ec, final String name) {
    }
}
