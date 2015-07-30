// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.spring;

import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import com.newrelic.agent.config.BaseConfig;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public abstract class MethodInvokerPointCut extends TracerFactoryPointCut
{
    private static final String TRANSACTION_NAMING_CONFIG_PARAMETER_NAME = "transaction_naming_scheme";
    private static final String SPRING_FRAMEWORK_CONFIG_PARAMETER_NAME = "spring_framework";
    private static final String CONTROLLER_METHOD_NAMING = "controller_method";
    private static final String VIEW_NAMING = "view";
    private static final String DEFAULT_NAMING_METHOD = "controller_method";
    protected static final String TO_REMOVE = "$$EnhancerBy";
    private final boolean useFullPackageName;
    private final boolean normalizeTransactions;
    private final boolean normalizationDisabled;
    
    public MethodInvokerPointCut(final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(new PointCutConfiguration("spring_handler_method_invoker"), classMatcher, methodMatcher);
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.useFullPackageName = getSpringConfiguration(config).getProperty("use_full_package_name", false);
        this.normalizeTransactions = "controller_method".equals(getSpringConfiguration(config).getProperty("transaction_naming_scheme", "controller_method"));
        this.normalizationDisabled = (!this.normalizeTransactions && !useViewNameToNormalize(config));
    }
    
    private static BaseConfig getSpringConfiguration(final AgentConfig config) {
        final Map<String, Object> props = config.getInstrumentationConfig().getProperty("spring_framework", Maps.<String, Object>newHashMap());
        return new BaseConfig(props);
    }
    
    static boolean useViewNameToNormalize(final AgentConfig config) {
        return "view".equals(getSpringConfiguration(config).getProperty("transaction_naming_scheme", "controller_method"));
    }
    
    protected boolean isNormalizeTransactions() {
        return this.normalizeTransactions;
    }
    
    protected boolean isNormalizationDisabled() {
        return this.normalizationDisabled;
    }
    
    protected boolean isUseFullPackageName() {
        return this.useFullPackageName;
    }
    
    protected void setTransactionName(final Transaction transaction, final String methodName, final Class<?> pController) {
        if (!transaction.isTransactionNamingEnabled()) {
            return;
        }
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        if (policy.canSetTransactionName(transaction, TransactionNamePriority.FRAMEWORK)) {
            final String controller = this.getControllerName(methodName, pController);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Spring controller", controller);
                Agent.LOG.finer(msg);
            }
            policy.setTransactionName(transaction, controller, "SpringController", TransactionNamePriority.FRAMEWORK);
        }
    }
    
    private String getControllerName(final String methodName, final Class<?> controller) {
        String controllerName = this.isUseFullPackageName() ? controller.getName() : controller.getSimpleName();
        final int indexOf = controllerName.indexOf("$$EnhancerBy");
        if (indexOf > 0) {
            controllerName = controllerName.substring(0, indexOf);
        }
        return '/' + controllerName + '/' + methodName;
    }
}
