// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.boolex;

import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EvaluationException;
import org.codehaus.groovy.control.CompilationFailedException;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyClassLoader;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.util.FileUtil;
import groovy.lang.Script;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluatorBase;

public class GEventEvaluator extends EventEvaluatorBase<ILoggingEvent>
{
    String expression;
    IEvaluator delegateEvaluator;
    Script script;
    
    public String getExpression() {
        return this.expression;
    }
    
    public void setExpression(final String expression) {
        this.expression = expression;
    }
    
    public void start() {
        int errors = 0;
        if (this.expression == null || this.expression.length() == 0) {
            this.addError("Empty expression");
            return;
        }
        this.addInfo("Expression to evaluate [" + this.expression + "]");
        final ClassLoader classLoader = this.getClass().getClassLoader();
        String currentPackageName = this.getClass().getPackage().getName();
        currentPackageName = currentPackageName.replace('.', '/');
        String scriptText = FileUtil.resourceAsString(this, classLoader, currentPackageName + "/EvaluatorTemplate.groovy");
        if (scriptText == null) {
            return;
        }
        scriptText = scriptText.replace("//EXPRESSION", this.expression);
        final GroovyClassLoader gLoader = new GroovyClassLoader(classLoader);
        try {
            final Class scriptClass = gLoader.parseClass(scriptText);
            final GroovyObject goo = scriptClass.newInstance();
            this.delegateEvaluator = (IEvaluator)goo;
        }
        catch (CompilationFailedException cfe) {
            this.addError("Failed to compile expression [" + this.expression + "]", (Throwable)cfe);
            ++errors;
        }
        catch (Exception e) {
            this.addError("Failed to compile expression [" + this.expression + "]", e);
            ++errors;
        }
        if (errors == 0) {
            super.start();
        }
    }
    
    public boolean evaluate(final ILoggingEvent event) throws NullPointerException, EvaluationException {
        return this.delegateEvaluator != null && this.delegateEvaluator.doEvaluate(event);
    }
}
