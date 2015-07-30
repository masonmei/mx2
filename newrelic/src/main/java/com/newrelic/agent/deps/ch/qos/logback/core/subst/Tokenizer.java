// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.subst;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer
{
    final String pattern;
    final int patternLength;
    TokenizerState state;
    int pointer;
    
    public Tokenizer(final String pattern) {
        this.state = TokenizerState.LITERAL_STATE;
        this.pointer = 0;
        this.pattern = pattern;
        this.patternLength = pattern.length();
    }
    
    List tokenize() throws ScanException {
        final List<Token> tokenList = new ArrayList<Token>();
        final StringBuilder buf = new StringBuilder();
        while (this.pointer < this.patternLength) {
            final char c = this.pattern.charAt(this.pointer);
            ++this.pointer;
            switch (this.state) {
                case LITERAL_STATE: {
                    this.handleLiteralState(c, tokenList, buf);
                    continue;
                }
                case START_STATE: {
                    this.handleStartState(c, tokenList, buf);
                    continue;
                }
                case DEFAULT_VAL_STATE: {
                    this.handleDefaultValueState(c, tokenList, buf);
                    continue;
                }
            }
        }
        switch (this.state) {
            case LITERAL_STATE: {
                this.addLiteralToken(tokenList, buf);
                break;
            }
            case START_STATE: {
                throw new ScanException("Unexpected end of pattern string");
            }
        }
        return tokenList;
    }
    
    private void handleDefaultValueState(final char c, final List<Token> tokenList, final StringBuilder stringBuilder) {
        switch (c) {
            case '-': {
                tokenList.add(Token.DEFAULT_SEP_TOKEN);
                this.state = TokenizerState.LITERAL_STATE;
                break;
            }
            case '$': {
                stringBuilder.append(':');
                this.addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                this.state = TokenizerState.START_STATE;
                break;
            }
            default: {
                stringBuilder.append(':').append(c);
                this.state = TokenizerState.LITERAL_STATE;
                break;
            }
        }
    }
    
    private void handleStartState(final char c, final List<Token> tokenList, final StringBuilder stringBuilder) {
        if (c == '{') {
            tokenList.add(Token.START_TOKEN);
        }
        else {
            stringBuilder.append('$').append(c);
        }
        this.state = TokenizerState.LITERAL_STATE;
    }
    
    private void handleLiteralState(final char c, final List<Token> tokenList, final StringBuilder stringBuilder) {
        if (c == '$') {
            this.addLiteralToken(tokenList, stringBuilder);
            stringBuilder.setLength(0);
            this.state = TokenizerState.START_STATE;
        }
        else if (c == ':') {
            this.addLiteralToken(tokenList, stringBuilder);
            stringBuilder.setLength(0);
            this.state = TokenizerState.DEFAULT_VAL_STATE;
        }
        else if (c == '{') {
            this.addLiteralToken(tokenList, stringBuilder);
            tokenList.add(Token.CURLY_LEFT_TOKEN);
            stringBuilder.setLength(0);
        }
        else if (c == '}') {
            this.addLiteralToken(tokenList, stringBuilder);
            tokenList.add(Token.CURLY_RIGHT_TOKEN);
            stringBuilder.setLength(0);
        }
        else {
            stringBuilder.append(c);
        }
    }
    
    private void addLiteralToken(final List<Token> tokenList, final StringBuilder stringBuilder) {
        if (stringBuilder.length() == 0) {
            return;
        }
        tokenList.add(new Token(Token.Type.LITERAL, stringBuilder.toString()));
    }
    
    enum TokenizerState
    {
        LITERAL_STATE, 
        START_STATE, 
        DEFAULT_VAL_STATE;
    }
}
