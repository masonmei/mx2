// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser;

import com.newrelic.agent.deps.ch.qos.logback.core.pattern.ReplacingCompositeConverter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.IdentityCompositeConverter;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.FormatInfo;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.Converter;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.IEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import java.util.List;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class Parser<E> extends ContextAwareBase
{
    public static final String MISSING_RIGHT_PARENTHESIS = "http://logback.qos.ch/codes.html#missingRightParenthesis";
    public static final Map<String, String> DEFAULT_COMPOSITE_CONVERTER_MAP;
    public static final String REPLACE_CONVERTER_WORD = "replace";
    final List tokenList;
    int pointer;
    
    Parser(final TokenStream ts) throws ScanException {
        this.pointer = 0;
        this.tokenList = ts.tokenize();
    }
    
    public Parser(final String pattern) throws ScanException {
        this(pattern, new RegularEscapeUtil());
    }
    
    public Parser(final String pattern, final IEscapeUtil escapeUtil) throws ScanException {
        this.pointer = 0;
        try {
            final TokenStream ts = new TokenStream(pattern, escapeUtil);
            this.tokenList = ts.tokenize();
        }
        catch (IllegalArgumentException npe) {
            throw new ScanException("Failed to initialize Parser", npe);
        }
    }
    
    public Converter<E> compile(final Node top, final Map converterMap) {
        final Compiler<E> compiler = new Compiler<E>(top, converterMap);
        compiler.setContext(this.context);
        return compiler.compile();
    }
    
    public Node parse() throws ScanException {
        return this.E();
    }
    
    Node E() throws ScanException {
        final Node t = this.T();
        if (t == null) {
            return null;
        }
        final Node eOpt = this.Eopt();
        if (eOpt != null) {
            t.setNext(eOpt);
        }
        return t;
    }
    
    Node Eopt() throws ScanException {
        final Token next = this.getCurentToken();
        if (next == null) {
            return null;
        }
        return this.E();
    }
    
    Node T() throws ScanException {
        final Token t = this.getCurentToken();
        this.expectNotNull(t, "a LITERAL or '%'");
        switch (t.getType()) {
            case 1000: {
                this.advanceTokenPointer();
                return new Node(0, t.getValue());
            }
            case 37: {
                this.advanceTokenPointer();
                final Token u = this.getCurentToken();
                this.expectNotNull(u, "a FORMAT_MODIFIER, SIMPLE_KEYWORD or COMPOUND_KEYWORD");
                FormattingNode c;
                if (u.getType() == 1002) {
                    final FormatInfo fi = FormatInfo.valueOf((String)u.getValue());
                    this.advanceTokenPointer();
                    c = this.C();
                    c.setFormatInfo(fi);
                }
                else {
                    c = this.C();
                }
                return c;
            }
            default: {
                return null;
            }
        }
    }
    
    FormattingNode C() throws ScanException {
        final Token t = this.getCurentToken();
        this.expectNotNull(t, "a LEFT_PARENTHESIS or KEYWORD");
        final int type = t.getType();
        switch (type) {
            case 1004: {
                return this.SINGLE();
            }
            case 1005: {
                this.advanceTokenPointer();
                return this.COMPOSITE(t.getValue().toString());
            }
            default: {
                throw new IllegalStateException("Unexpected token " + t);
            }
        }
    }
    
    FormattingNode SINGLE() throws ScanException {
        final Token t = this.getNextToken();
        final SimpleKeywordNode keywordNode = new SimpleKeywordNode(t.getValue());
        final Token ot = this.getCurentToken();
        if (ot != null && ot.getType() == 1006) {
            final List<String> optionList = (List<String>)ot.getValue();
            keywordNode.setOptions(optionList);
            this.advanceTokenPointer();
        }
        return keywordNode;
    }
    
    FormattingNode COMPOSITE(final String keyword) throws ScanException {
        final CompositeNode compositeNode = new CompositeNode(keyword);
        final Node childNode = this.E();
        compositeNode.setChildNode(childNode);
        final Token t = this.getNextToken();
        if (t == null || t.getType() != 41) {
            final String msg = "Expecting RIGHT_PARENTHESIS token but got " + t;
            this.addError(msg);
            this.addError("See also http://logback.qos.ch/codes.html#missingRightParenthesis");
            throw new ScanException(msg);
        }
        final Token ot = this.getCurentToken();
        if (ot != null && ot.getType() == 1006) {
            final List<String> optionList = (List<String>)ot.getValue();
            compositeNode.setOptions(optionList);
            this.advanceTokenPointer();
        }
        return compositeNode;
    }
    
    Token getNextToken() {
        if (this.pointer < this.tokenList.size()) {
            return this.tokenList.get(this.pointer++);
        }
        return null;
    }
    
    Token getCurentToken() {
        if (this.pointer < this.tokenList.size()) {
            return this.tokenList.get(this.pointer);
        }
        return null;
    }
    
    void advanceTokenPointer() {
        ++this.pointer;
    }
    
    void expectNotNull(final Token t, final String expected) {
        if (t == null) {
            throw new IllegalStateException("All tokens consumed but was expecting " + expected);
        }
    }
    
    static {
        (DEFAULT_COMPOSITE_CONVERTER_MAP = new HashMap<String, String>()).put(Token.BARE_COMPOSITE_KEYWORD_TOKEN.getValue().toString(), IdentityCompositeConverter.class.getName());
        Parser.DEFAULT_COMPOSITE_CONVERTER_MAP.put("replace", ReplacingCompositeConverter.class.getName());
    }
}
