// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.regex.Matcher;
import java.util.logging.Level;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.util.Iterator;
import java.util.Set;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import java.util.regex.Pattern;
import com.newrelic.agent.tracers.AbstractTracerFactory;

public class GeneratorVisitTracerFactory extends AbstractTracerFactory
{
    protected static String RUM_STATE_PROCESSOR_KEY;
    private static String UNKNOWN_JSP;
    private static final Pattern COMMENT_PATTERN;
    
    static boolean isAutoInstrumentationEnabled() {
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        return config.getBrowserMonitoringConfig().isAutoInstrumentEnabled();
    }
    
    public static void noticeJspCompile(final Transaction tx, final String page) {
        tx.getInternalParameters().put(RUMStateProcessor.class.getName(), new RUMStateProcessor(page));
    }
    
    public static String getPage(final Transaction tx) {
        final RUMStateProcessor processor = tx.getInternalParameters().get(GeneratorVisitTracerFactory.RUM_STATE_PROCESSOR_KEY);
        return processor.getPage();
    }
    
    public static boolean isIgnorePageForAutoInstrument(final String page, final Transaction tx) {
        final Set<String> pages = ServiceFactory.getConfigService().getDefaultAgentConfig().getBrowserMonitoringConfig().getDisabledAutoPages();
        if (page != null) {
            for (final String current : pages) {
                if (current.equals(page)) {
                    logIgnoredPage(page, tx);
                    return true;
                }
            }
        }
        return false;
    }
    
    private static void logIgnoredPage(final String page, final Transaction tx) {
        if (tx != null && tx.getInternalParameters().get(page) == null) {
            tx.getInternalParameters().put(page, Boolean.TRUE);
            Agent.LOG.fine(MessageFormat.format("Ignoring page {0} for auto RUM instrumentation", page));
        }
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object generator, final Object[] args) {
        if (!isAutoInstrumentationEnabled()) {
            return null;
        }
        final Object node = args[0];
        try {
            final String page = getPage(transaction);
            if (isIgnorePageForAutoInstrument(page, transaction)) {
                return null;
            }
            final JasperClassFactory factory = JasperClassFactory.getJasperClassFactory(generator.getClass().getClassLoader());
            final GenerateVisitor generateVisitor = factory.getGenerateVisitor(generator);
            final TemplateText templateText = factory.getTemplateText(node);
            this.processText(transaction, generateVisitor, templateText);
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINE, "An error occurred auto enabling real user monitoring", e);
        }
        return null;
    }
    
    void processText(final Transaction tx, final GenerateVisitor generator, final TemplateText node) throws Exception {
        final String text = node.getText();
        if (text != null && text.length() > 0) {
            RUMStateProcessor state = tx.getInternalParameters().get(GeneratorVisitTracerFactory.RUM_STATE_PROCESSOR_KEY);
            if (state == null) {
                state = new RUMStateProcessor(GeneratorVisitTracerFactory.UNKNOWN_JSP);
                tx.getInternalParameters().put(RUMStateProcessor.class.getName(), state);
            }
            state.process(tx, generator, node, text);
        }
    }
    
    static {
        GeneratorVisitTracerFactory.RUM_STATE_PROCESSOR_KEY = RUMStateProcessor.class.getName();
        GeneratorVisitTracerFactory.UNKNOWN_JSP = "UNKNOWN";
        COMMENT_PATTERN = Pattern.compile("<!\\s*--.*?--\\s*\\>", 34);
    }
    
    private static class RUMStateProcessor extends AbstractRUMState
    {
        private final String page;
        private boolean inProgress;
        private RUMState currentState;
        
        private RUMStateProcessor(final String page) {
            this.inProgress = false;
            this.currentState = RUMStateProcessor.HEAD_STATE;
            this.page = page;
        }
        
        String getPage() {
            return this.page;
        }
        
        public RUMState process(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
            if (this.inProgress) {
                return this;
            }
            try {
                this.inProgress = true;
                int start = 0;
                final Matcher matcher = GeneratorVisitTracerFactory.COMMENT_PATTERN.matcher(text);
                while (matcher.find()) {
                    final String comment = text.substring(matcher.start(), matcher.end());
                    final String notComment = text.substring(start, matcher.start());
                    start = matcher.end();
                    if (notComment.length() > 0) {
                        this.currentState = this.currentState.process(tx, generator, node, notComment);
                    }
                    this.writeText(tx, generator, node, comment);
                }
                final String notComment2 = text.substring(start, text.length());
                if (notComment2.length() > 0) {
                    this.currentState = this.currentState.process(tx, generator, node, notComment2);
                }
                this.inProgress = false;
            }
            finally {
                this.inProgress = false;
            }
            return this;
        }
    }
}
