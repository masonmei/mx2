// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.jasper;

import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.Transaction;
import java.util.regex.Pattern;

public abstract class AbstractRUMState implements RUMState
{
    protected static final String BROWSER_TIMING_HEADER_CODE_SNIPPET;
    protected static final String BROWSER_TIMING_FOOTER_CODE_SNIPPET;
    protected static final Pattern HEAD_PATTERN;
    protected static final Pattern HEAD_END_PATTERN;
    protected static final Pattern BODY_END_PATTERN;
    protected static final Pattern BODY_START_PATTERN;
    protected static final Pattern HTML_END_PATTERN;
    protected static final Pattern NOT_META_PATTERN;
    protected static final Pattern SCRIPT_PATTERN;
    protected static final Pattern END_SCRIPT_PATTERN;
    protected static final Pattern START_TAG_PATTERN;
    protected static final Pattern END_TAG_OR_QUOTE_PATTERN;
    protected static final Pattern QUOTE_PATTERN;
    protected static final Pattern SINGLE_QUOTE_PATTERN;
    protected static final Pattern END_COMMENT;
    protected static final Pattern TITLE_END;
    protected static final RUMState HEAD_STATE;
    protected static final RUMState PRE_META_STATE;
    protected static final RUMState QUOTE_STATE;
    protected static final RUMState SINGLE_QUOTE_STATE;
    protected static final RUMState COMMENT_STATE;
    protected static final RUMState TITLE_STATE;
    protected static final RUMState META_STATE;
    protected static final RUMState BODY_STATE;
    protected static final RUMState DONE_STATE;
    protected static final RUMState SCRIPT_STATE;
    protected static final RUMState PRE_HEAD_SCRIPT_STATE;
    
    protected static String getMethodInvocationCode(final String methodName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("try {\n");
        sb.append("  java.lang.Class __nrClass = Class.forName(\"com.newrelic.api.agent.NewRelicApiImplementation\");\n");
        sb.append("  java.lang.reflect.Method __nrMethod = __nrClass.getMethod(\"").append(methodName).append("\", new Class[]{ String.class });\n");
        sb.append("  out.write((String) __nrMethod.invoke(null, new Object[]{ null }));\n");
        sb.append("} catch (Throwable __t) {\n");
        sb.append("}\n");
        return sb.toString();
    }
    
    protected void writeText(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text) throws Exception {
        node.setText(text);
        generator.visit(node);
        node.setText("");
    }
    
    protected void writeHeader(final GenerateVisitor generator) throws Exception {
        generator.writeScriptlet(AbstractRUMState.BROWSER_TIMING_HEADER_CODE_SNIPPET);
    }
    
    protected void writeFooter(final GenerateVisitor generator) throws Exception {
        generator.writeScriptlet(AbstractRUMState.BROWSER_TIMING_FOOTER_CODE_SNIPPET);
    }
    
    protected void writeHeader(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text, final int end) throws Exception {
        final String jspFile = GeneratorVisitTracerFactory.getPage(tx);
        final String msg = MessageFormat.format("Injecting browser timing header into: {0}", jspFile);
        Agent.LOG.fine(msg);
        String s = text.substring(0, end);
        node.setText(s);
        generator.visit(node);
        generator.writeScriptlet(AbstractRUMState.BROWSER_TIMING_HEADER_CODE_SNIPPET);
        s = text.substring(end);
        node.setText(s);
        generator.visit(node);
        node.setText("");
    }
    
    protected void writeFooter(final Transaction tx, final GenerateVisitor generator, final TemplateText node, final String text, final int end) throws Exception {
        final String jspFile = GeneratorVisitTracerFactory.getPage(tx);
        final String msg = MessageFormat.format("Injecting browser timing footer into: {0}", jspFile);
        Agent.LOG.fine(msg);
        String s = text.substring(0, end);
        node.setText(s);
        generator.visit(node);
        generator.writeScriptlet(AbstractRUMState.BROWSER_TIMING_FOOTER_CODE_SNIPPET);
        s = text.substring(end);
        node.setText(s);
        generator.visit(node);
        node.setText("");
    }
    
    static {
        BROWSER_TIMING_HEADER_CODE_SNIPPET = getMethodInvocationCode("getBrowserTimingHeaderForContentType");
        BROWSER_TIMING_FOOTER_CODE_SNIPPET = getMethodInvocationCode("getBrowserTimingFooterForContentType");
        HEAD_PATTERN = Pattern.compile("<head[^>]*>", 34);
        HEAD_END_PATTERN = Pattern.compile("</head[^>]*>", 34);
        BODY_END_PATTERN = Pattern.compile("</body[^>]*>", 34);
        BODY_START_PATTERN = Pattern.compile("<body[^>]*>", 34);
        HTML_END_PATTERN = Pattern.compile("</html[^>]*>", 34);
        NOT_META_PATTERN = Pattern.compile("<(?![mM][eE][tT][aA]\\s)", 34);
        SCRIPT_PATTERN = Pattern.compile("<script", 34);
        END_SCRIPT_PATTERN = Pattern.compile("</script>", 34);
        START_TAG_PATTERN = Pattern.compile("<");
        END_TAG_OR_QUOTE_PATTERN = Pattern.compile("(>|\"|')");
        QUOTE_PATTERN = Pattern.compile("\"", 34);
        SINGLE_QUOTE_PATTERN = Pattern.compile("'", 34);
        END_COMMENT = Pattern.compile("-->", 34);
        TITLE_END = Pattern.compile("</title>", 34);
        HEAD_STATE = new HeadState();
        PRE_META_STATE = new PreMetaState();
        QUOTE_STATE = new QuoteSate();
        SINGLE_QUOTE_STATE = new SingleQuoteState();
        COMMENT_STATE = new CommentState();
        TITLE_STATE = new TitleState();
        META_STATE = new MetaState();
        BODY_STATE = new BodyState();
        DONE_STATE = new DoneState();
        SCRIPT_STATE = new ScriptPostHeaderState();
        PRE_HEAD_SCRIPT_STATE = new ScriptPreHeaderState();
    }
}
