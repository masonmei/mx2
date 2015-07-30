// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.RestrictedEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.IEscapeUtil;

class TokenStream
{
    final String pattern;
    final int patternLength;
    final IEscapeUtil escapeUtil;
    final IEscapeUtil optionEscapeUtil;
    TokenizerState state;
    int pointer;
    
    TokenStream(final String pattern) {
        this(pattern, new RegularEscapeUtil());
    }
    
    TokenStream(final String pattern, final IEscapeUtil escapeUtil) {
        this.optionEscapeUtil = new RestrictedEscapeUtil();
        this.state = TokenizerState.LITERAL_STATE;
        this.pointer = 0;
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("null or empty pattern string not allowed");
        }
        this.pattern = pattern;
        this.patternLength = pattern.length();
        this.escapeUtil = escapeUtil;
    }
    
    List tokenize() throws ScanException {
        final List<Token> tokenList = new ArrayList<Token>();
        final StringBuffer buf = new StringBuffer();
        while (this.pointer < this.patternLength) {
            final char c = this.pattern.charAt(this.pointer);
            ++this.pointer;
            switch (this.state) {
                case LITERAL_STATE: {
                    this.handleLiteralState(c, tokenList, buf);
                    continue;
                }
                case FORMAT_MODIFIER_STATE: {
                    this.handleFormatModifierState(c, tokenList, buf);
                    continue;
                }
                case OPTION_STATE: {
                    this.processOption(c, tokenList, buf);
                    continue;
                }
                case KEYWORD_STATE: {
                    this.handleKeywordState(c, tokenList, buf);
                    continue;
                }
                case RIGHT_PARENTHESIS_STATE: {
                    this.handleRightParenthesisState(c, tokenList, buf);
                    continue;
                }
            }
        }
        switch (this.state) {
            case LITERAL_STATE: {
                this.addValuedToken(1000, buf, tokenList);
                break;
            }
            case KEYWORD_STATE: {
                tokenList.add(new Token(1004, buf.toString()));
                break;
            }
            case RIGHT_PARENTHESIS_STATE: {
                tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
                break;
            }
            case FORMAT_MODIFIER_STATE:
            case OPTION_STATE: {
                throw new ScanException("Unexpected end of pattern string");
            }
        }
        return tokenList;
    }
    
    private void handleRightParenthesisState(final char c, final List<Token> tokenList, final StringBuffer buf) {
        tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
        switch (c) {
            case ')': {
                break;
            }
            case '{': {
                this.state = TokenizerState.OPTION_STATE;
                break;
            }
            case '\\': {
                this.escape("%{}", buf);
                this.state = TokenizerState.LITERAL_STATE;
                break;
            }
            default: {
                buf.append(c);
                this.state = TokenizerState.LITERAL_STATE;
                break;
            }
        }
    }
    
    private void processOption(final char c, final List<Token> tokenList, final StringBuffer buf) throws ScanException {
        final OptionTokenizer ot = new OptionTokenizer(this);
        ot.tokenize(c, tokenList);
    }
    
    private void handleFormatModifierState(final char c, final List<Token> tokenList, final StringBuffer buf) {
        if (c == '(') {
            this.addValuedToken(1002, buf, tokenList);
            tokenList.add(Token.BARE_COMPOSITE_KEYWORD_TOKEN);
            this.state = TokenizerState.LITERAL_STATE;
        }
        else if (Character.isJavaIdentifierStart(c)) {
            this.addValuedToken(1002, buf, tokenList);
            this.state = TokenizerState.KEYWORD_STATE;
            buf.append(c);
        }
        else {
            buf.append(c);
        }
    }
    
    private void handleLiteralState(final char c, final List<Token> tokenList, final StringBuffer buf) {
        switch (c) {
            case '\\': {
                this.escape("%()", buf);
                break;
            }
            case '%': {
                this.addValuedToken(1000, buf, tokenList);
                tokenList.add(Token.PERCENT_TOKEN);
                this.state = TokenizerState.FORMAT_MODIFIER_STATE;
                break;
            }
            case ')': {
                this.addValuedToken(1000, buf, tokenList);
                this.state = TokenizerState.RIGHT_PARENTHESIS_STATE;
                break;
            }
            default: {
                buf.append(c);
                break;
            }
        }
    }
    
    private void handleKeywordState(final char c, final List<Token> tokenList, final StringBuffer buf) {
        if (Character.isJavaIdentifierPart(c)) {
            buf.append(c);
        }
        else if (c == '{') {
            this.addValuedToken(1004, buf, tokenList);
            this.state = TokenizerState.OPTION_STATE;
        }
        else if (c == '(') {
            this.addValuedToken(1005, buf, tokenList);
            this.state = TokenizerState.LITERAL_STATE;
        }
        else if (c == '%') {
            this.addValuedToken(1004, buf, tokenList);
            tokenList.add(Token.PERCENT_TOKEN);
            this.state = TokenizerState.FORMAT_MODIFIER_STATE;
        }
        else if (c == ')') {
            this.addValuedToken(1004, buf, tokenList);
            this.state = TokenizerState.RIGHT_PARENTHESIS_STATE;
        }
        else {
            this.addValuedToken(1004, buf, tokenList);
            if (c == '\\') {
                if (this.pointer < this.patternLength) {
                    final char next = this.pattern.charAt(this.pointer++);
                    this.escapeUtil.escape("%()", buf, next, this.pointer);
                }
            }
            else {
                buf.append(c);
            }
            this.state = TokenizerState.LITERAL_STATE;
        }
    }
    
    void escape(final String escapeChars, final StringBuffer buf) {
        if (this.pointer < this.patternLength) {
            final char next = this.pattern.charAt(this.pointer++);
            this.escapeUtil.escape(escapeChars, buf, next, this.pointer);
        }
    }
    
    void optionEscape(final String escapeChars, final StringBuffer buf) {
        if (this.pointer < this.patternLength) {
            final char next = this.pattern.charAt(this.pointer++);
            this.optionEscapeUtil.escape(escapeChars, buf, next, this.pointer);
        }
    }
    
    private void addValuedToken(final int type, final StringBuffer buf, final List<Token> tokenList) {
        if (buf.length() > 0) {
            tokenList.add(new Token(type, buf.toString()));
            buf.setLength(0);
        }
    }
    
    enum TokenizerState
    {
        LITERAL_STATE, 
        FORMAT_MODIFIER_STATE, 
        KEYWORD_STATE, 
        OPTION_STATE, 
        RIGHT_PARENTHESIS_STATE;
    }
}
