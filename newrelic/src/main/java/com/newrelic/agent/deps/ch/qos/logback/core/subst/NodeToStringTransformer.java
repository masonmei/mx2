// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.subst;

import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PropertyContainer;

public class NodeToStringTransformer
{
    final Node node;
    final PropertyContainer propertyContainer0;
    final PropertyContainer propertyContainer1;
    
    public NodeToStringTransformer(final Node node, final PropertyContainer propertyContainer0, final PropertyContainer propertyContainer1) {
        this.node = node;
        this.propertyContainer0 = propertyContainer0;
        this.propertyContainer1 = propertyContainer1;
    }
    
    public NodeToStringTransformer(final Node node, final PropertyContainer propertyContainer0) {
        this(node, propertyContainer0, null);
    }
    
    public static String substituteVariable(final String input, final PropertyContainer pc0, final PropertyContainer pc1) throws ScanException {
        final Tokenizer tokenizer = new Tokenizer(input);
        final Parser parser = new Parser(tokenizer.tokenize());
        final Node node = parser.parse();
        final NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, pc0, pc1);
        return nodeToStringTransformer.transform();
    }
    
    public String transform() {
        final StringBuilder stringBuilder = new StringBuilder();
        this.compileNode(this.node, stringBuilder);
        return stringBuilder.toString();
    }
    
    private void compileNode(final Node inputNode, final StringBuilder stringBuilder) {
        for (Node n = inputNode; n != null; n = n.next) {
            switch (n.type) {
                case LITERAL: {
                    this.handleLiteral(n, stringBuilder);
                    break;
                }
                case VARIABLE: {
                    this.handleVariable(n, stringBuilder);
                    break;
                }
            }
        }
    }
    
    private void handleVariable(final Node n, final StringBuilder stringBuilder) {
        final StringBuilder keyBuffer = new StringBuilder();
        final Node payload = (Node)n.payload;
        this.compileNode(payload, keyBuffer);
        final String key = keyBuffer.toString();
        final String value = this.lookupKey(key);
        if (value != null) {
            stringBuilder.append(value);
            return;
        }
        if (n.defaultPart == null) {
            stringBuilder.append(key + "_IS_UNDEFINED");
            return;
        }
        final Node defaultPart = (Node)n.defaultPart;
        final StringBuilder defaultPartBuffer = new StringBuilder();
        this.compileNode(defaultPart, defaultPartBuffer);
        final String defaultVal = defaultPartBuffer.toString();
        stringBuilder.append(defaultVal);
    }
    
    private String lookupKey(final String key) {
        String value = this.propertyContainer0.getProperty(key);
        if (value != null) {
            return value;
        }
        if (this.propertyContainer1 != null) {
            value = this.propertyContainer1.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        value = OptionHelper.getSystemProperty(key, null);
        if (value != null) {
            return value;
        }
        value = OptionHelper.getEnv(key);
        if (value != null) {
            return value;
        }
        return null;
    }
    
    private void handleLiteral(final Node n, final StringBuilder stringBuilder) {
        stringBuilder.append((String)n.payload);
    }
}
