// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern.parser;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.AsIsEscapeUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.util.IEscapeUtil;

public class OptionTokenizer
{
    private static final int EXPECTING_STATE = 0;
    private static final int RAW_COLLECTING_STATE = 1;
    private static final int QUOTED_COLLECTING_STATE = 2;
    final IEscapeUtil escapeUtil;
    final TokenStream tokenStream;
    final String pattern;
    final int patternLength;
    char quoteChar;
    int state;
    
    OptionTokenizer(final TokenStream tokenStream) {
        this(tokenStream, new AsIsEscapeUtil());
    }
    
    OptionTokenizer(final TokenStream tokenStream, final IEscapeUtil escapeUtil) {
        this.state = 0;
        this.tokenStream = tokenStream;
        this.pattern = tokenStream.pattern;
        this.patternLength = tokenStream.patternLength;
        this.escapeUtil = escapeUtil;
    }
    
    void tokenize(final char firstChar, final List<Token> tokenList) throws ScanException {
        final StringBuffer buf = new StringBuffer();
        final List<String> optionList = new ArrayList<String>();
        char c = firstChar;
        while (this.tokenStream.pointer < this.patternLength) {
            Label_0332: {
                switch (this.state) {
                    case 0: {
                        switch (c) {
                            case '\t':
                            case '\n':
                            case '\r':
                            case ' ':
                            case ',': {
                                break Label_0332;
                            }
                            case '\"':
                            case '\'': {
                                this.state = 2;
                                this.quoteChar = c;
                                break Label_0332;
                            }
                            case '}': {
                                this.emitOptionToken(tokenList, optionList);
                                return;
                            }
                            default: {
                                buf.append(c);
                                this.state = 1;
                                break Label_0332;
                            }
                        }
                        break;
                    }
                    case 1: {
                        switch (c) {
                            case ',': {
                                optionList.add(buf.toString().trim());
                                buf.setLength(0);
                                this.state = 0;
                                break Label_0332;
                            }
                            case '}': {
                                optionList.add(buf.toString().trim());
                                this.emitOptionToken(tokenList, optionList);
                                return;
                            }
                            default: {
                                buf.append(c);
                                break Label_0332;
                            }
                        }
                        break;
                    }
                    case 2: {
                        if (c == this.quoteChar) {
                            optionList.add(buf.toString());
                            buf.setLength(0);
                            this.state = 0;
                            break;
                        }
                        if (c == '\\') {
                            this.escape(String.valueOf(this.quoteChar), buf);
                            break;
                        }
                        buf.append(c);
                        break;
                    }
                }
            }
            c = this.pattern.charAt(this.tokenStream.pointer);
            final TokenStream tokenStream = this.tokenStream;
            ++tokenStream.pointer;
        }
        if (c == '}') {
            if (this.state == 0) {
                this.emitOptionToken(tokenList, optionList);
            }
            else {
                if (this.state != 1) {
                    throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
                }
                optionList.add(buf.toString().trim());
                this.emitOptionToken(tokenList, optionList);
            }
            return;
        }
        throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
    }
    
    void emitOptionToken(final List<Token> tokenList, final List<String> optionList) {
        tokenList.add(new Token(1006, optionList));
        this.tokenStream.state = TokenStream.TokenizerState.LITERAL_STATE;
    }
    
    void escape(final String escapeChars, final StringBuffer buf) {
        if (this.tokenStream.pointer < this.patternLength) {
            final char next = this.pattern.charAt(this.tokenStream.pointer++);
            this.escapeUtil.escape(escapeChars, buf, next, this.tokenStream.pointer);
        }
    }
}
