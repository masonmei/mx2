// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import java.util.logging.Level;
import com.newrelic.agent.tracers.MethodExitTracerNoSkip;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.AbstractTracerFactory;

public class GeneratorVisitBodyTracerFactory extends AbstractTracerFactory
{
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object visitorObj, final Object[] args) {
        if (!GeneratorVisitTracerFactory.isAutoInstrumentationEnabled()) {
            return null;
        }
        try {
            final String page = GeneratorVisitTracerFactory.getPage(transaction);
            if (GeneratorVisitTracerFactory.isIgnorePageForAutoInstrument(page, transaction)) {
                return null;
            }
            final JasperClassFactory factory = JasperClassFactory.getJasperClassFactory(visitorObj.getClass().getClassLoader());
            final Node node = factory.getNode(args[0]);
            final Visitor visitor = factory.getVisitor(visitorObj);
            final String nodeName = node.getQName();
            if ("head".equals(nodeName.toLowerCase()) && this.checkParentNode(node, "html")) {
                Agent.LOG.fine("Compiling the browser timing header into a jsp");
                visitor.writeScriptlet(AbstractRUMState.BROWSER_TIMING_HEADER_CODE_SNIPPET);
            }
            else if ("body".equals(nodeName.toLowerCase()) && this.checkParentNode(node, "html")) {
                return new MethodExitTracerNoSkip(sig, transaction) {
                    protected void doFinish(final int opcode, final Object returnValue) {
                        GeneratorVisitBodyTracerFactory.this.writeFooter(visitor);
                    }
                };
            }
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINE, "An error occurred auto enabling real user monitoring", e);
        }
        return null;
    }
    
    private void writeFooter(final Visitor visitor) {
        Agent.LOG.fine("Compiling the browser timing footer into a jsp");
        try {
            visitor.writeScriptlet(AbstractRUMState.BROWSER_TIMING_FOOTER_CODE_SNIPPET);
        }
        catch (Exception ex) {
            Agent.LOG.log(Level.FINE, "An error occurred auto enabling real user monitoring", ex);
        }
    }
    
    private boolean checkParentNode(final Node node, final String name) throws Exception {
        final Node parent = node.getParent();
        return parent != null && name.equals(parent.getQName());
    }
}
