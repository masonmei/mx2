// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.subst;

import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import java.util.List;

public class Parser
{
    final List<Token> tokenList;
    int pointer;
    
    public Parser(final List<Token> tokenList) {
        this.pointer = 0;
        this.tokenList = tokenList;
    }
    
    public Node parse() throws ScanException {
        return this.E();
    }
    
    private Node E() throws ScanException {
        final Node t = this.T();
        if (t == null) {
            return null;
        }
        final Node eOpt = this.Eopt();
        if (eOpt != null) {
            this.appendNode(t, eOpt);
        }
        return t;
    }
    
    private Node Eopt() throws ScanException {
        final Token next = this.getCurentToken();
        if (next == null) {
            return null;
        }
        return this.E();
    }
    
    private Node T() throws ScanException {
        final Token t = this.getCurentToken();
        switch (t.type) {
            case LITERAL: {
                this.advanceTokenPointer();
                return new Node(Node.Type.LITERAL, t.payload);
            }
            case CURLY_LEFT: {
                this.advanceTokenPointer();
                final Node inner = this.E();
                final Token right = this.getCurentToken();
                this.expectCurlyRight(right);
                this.advanceTokenPointer();
                final Node curlyLeft = new Node(Node.Type.LITERAL, CoreConstants.LEFT_ACCOLADE);
                curlyLeft.next = inner;
                final Node curlyRightNode = new Node(Node.Type.LITERAL, CoreConstants.RIGHT_ACCOLADE);
                if (inner == null) {
                    curlyLeft.next = curlyRightNode;
                }
                else {
                    this.appendNode(inner, curlyRightNode);
                }
                return curlyLeft;
            }
            case START: {
                this.advanceTokenPointer();
                final Node v = this.V();
                final Token w = this.getCurentToken();
                this.expectCurlyRight(w);
                this.advanceTokenPointer();
                return v;
            }
            default: {
                return null;
            }
        }
    }
    
    private void appendNode(final Node node, final Node additionalNode) {
        Node n;
        for (n = node; n.next != null; n = n.next) {}
        n.next = additionalNode;
    }
    
    private Node V() throws ScanException {
        final Node e = this.E();
        final Node variable = new Node(Node.Type.VARIABLE, e);
        final Token t = this.getCurentToken();
        if (t != null && t.type == Token.Type.DEFAULT) {
            this.advanceTokenPointer();
            final Node def = this.E();
            variable.defaultPart = def;
        }
        return variable;
    }
    
    void advanceTokenPointer() {
        ++this.pointer;
    }
    
    void expectNotNull(final Token t, final String expected) {
        if (t == null) {
            throw new IllegalArgumentException("All tokens consumed but was expecting \"" + expected + "\"");
        }
    }
    
    void expectCurlyRight(final Token t) throws ScanException {
        this.expectNotNull(t, "}");
        if (t.type != Token.Type.CURLY_RIGHT) {
            throw new ScanException("Expecting }");
        }
    }
    
    Token getCurentToken() {
        if (this.pointer < this.tokenList.size()) {
            return this.tokenList.get(this.pointer);
        }
        return null;
    }
}
