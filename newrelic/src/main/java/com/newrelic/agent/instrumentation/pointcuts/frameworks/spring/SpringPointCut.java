// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.spring;

import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import java.util.regex.Matcher;
import java.lang.reflect.InvocationTargetException;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import java.util.regex.Pattern;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SpringPointCut extends TracerFactoryPointCut
{
    public static final String SPRING_CONTROLLER = "SpringController";
    public static final String SPRING_VIEW = "SpringView";
    private static final String REDIRECT_VIEW_SYNTAX = "/redirect:";
    private static final String FORWARD_VIEW_SYNTAX = "/forward:";
    private static final Pattern HTTP_PATTERN;
    private final boolean normalizeTransactions;
    
    public SpringPointCut(final ClassTransformer ct) {
        super(SpringPointCut.class, new InterfaceMatcher("org/springframework/web/servlet/HandlerAdapter"), new ExactMethodMatcher("handle", "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/Object;)Lorg/springframework/web/servlet/ModelAndView;"));
        this.normalizeTransactions = MethodInvokerPointCut.useViewNameToNormalize(ServiceFactory.getConfigService().getDefaultAgentConfig());
    }
    
    static String getModelAndViewViewName(final Object modelAndView) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String viewName;
        if (modelAndView instanceof ModelAndView) {
            viewName = ((ModelAndView)modelAndView).getViewName();
        }
        else {
            viewName = (String)modelAndView.getClass().getMethod("getViewName", (Class<?>[])new Class[0]).invoke(modelAndView, new Object[0]);
        }
        return cleanModelAndViewName(viewName);
    }
    
    static String cleanModelAndViewName(String viewName) {
        if (viewName == null || viewName.length() == 0) {
            return viewName;
        }
        if (viewName.charAt(0) != '/') {
            viewName = '/' + viewName;
        }
        if (viewName.startsWith("/redirect:")) {
            return "/redirect:*";
        }
        if (viewName.startsWith("/forward:")) {
            return null;
        }
        viewName = ServiceFactory.getNormalizationService().getUrlBeforeParameters(viewName);
        final Matcher paramDelimiterMatcher = SpringPointCut.HTTP_PATTERN.matcher(viewName);
        if (paramDelimiterMatcher.matches()) {
            viewName = paramDelimiterMatcher.group(1) + '*';
        }
        return viewName;
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object controller, final Object[] args) {
        final Object handler = args[2];
        return new DefaultTracer(transaction, sig, controller) {
            protected void doFinish(final int opcode, final Object modelView) {
                if (modelView != null && SpringPointCut.this.normalizeTransactions) {
                    this.setTransactionName(transaction, modelView);
                }
                String metricName;
                if (handler != null) {
                    final StringBuilder tracerName = new StringBuilder("SpringController/");
                    tracerName.append(this.getControllerName(handler.getClass()));
                    metricName = tracerName.toString();
                }
                else {
                    final StringBuilder tracerName = new StringBuilder("SpringController/");
                    tracerName.append(this.getControllerName(controller.getClass()));
                    tracerName.append('/').append(sig.getMethodName());
                    metricName = tracerName.toString();
                }
                this.setMetricNameFormat(new SimpleMetricNameFormat(metricName));
                super.doFinish(opcode, modelView);
            }
            
            private String getControllerName(final Class<?> controller) {
                String controllerName = controller.getName();
                final int indexOf = controllerName.indexOf("$$EnhancerBy");
                if (indexOf > 0) {
                    controllerName = controllerName.substring(0, indexOf);
                }
                return controllerName;
            }
            
            private void setTransactionName(final Transaction transaction, final Object modelView) {
                if (!transaction.isTransactionNamingEnabled()) {
                    return;
                }
                final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
                if (policy.canSetTransactionName(transaction, TransactionNamePriority.FRAMEWORK)) {
                    final String modelAndViewName = this.doGetModelAndViewName(modelView);
                    if (modelAndViewName == null) {
                        return;
                    }
                    if (Agent.LOG.isLoggable(Level.FINER)) {
                        final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Spring ModelView", modelAndViewName);
                        Agent.LOG.finer(msg);
                    }
                    policy.setTransactionName(transaction, modelAndViewName, "SpringView", TransactionNamePriority.FRAMEWORK);
                }
            }
            
            private String doGetModelAndViewName(final Object modelAndView) {
                try {
                    return SpringPointCut.getModelAndViewViewName(modelAndView);
                }
                catch (Exception e) {
                    Agent.LOG.log(Level.FINE, "Unable to parse Spring ModelView", e);
                    return null;
                }
            }
        };
    }
    
    public boolean isEnabled() {
        return ServiceFactory.getConfigService().getDefaultAgentConfig().getProperty("enable_spring_tracing", true);
    }
    
    static {
        HTTP_PATTERN = Pattern.compile("(.*)https?://.*");
    }
}
