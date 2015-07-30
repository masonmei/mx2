// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.boolex;

import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class Matcher extends ContextAwareBase implements LifeCycle
{
    private String regex;
    private String name;
    private boolean caseSensitive;
    private boolean canonEq;
    private boolean unicodeCase;
    private boolean start;
    private Pattern pattern;
    
    public Matcher() {
        this.caseSensitive = true;
        this.canonEq = false;
        this.unicodeCase = false;
        this.start = false;
    }
    
    public String getRegex() {
        return this.regex;
    }
    
    public void setRegex(final String regex) {
        this.regex = regex;
    }
    
    public void start() {
        if (this.name == null) {
            this.addError("All Matcher objects must be named");
            return;
        }
        try {
            int code = 0;
            if (!this.caseSensitive) {
                code |= 0x2;
            }
            if (this.canonEq) {
                code |= 0x80;
            }
            if (this.unicodeCase) {
                code |= 0x40;
            }
            this.pattern = Pattern.compile(this.regex, code);
            this.start = true;
        }
        catch (PatternSyntaxException pse) {
            this.addError("Failed to compile regex [" + this.regex + "]", pse);
        }
    }
    
    public void stop() {
        this.start = false;
    }
    
    public boolean isStarted() {
        return this.start;
    }
    
    public boolean matches(final String input) throws EvaluationException {
        if (this.start) {
            final java.util.regex.Matcher matcher = this.pattern.matcher(input);
            return matcher.find();
        }
        throw new EvaluationException("Matcher [" + this.regex + "] not started");
    }
    
    public boolean isCanonEq() {
        return this.canonEq;
    }
    
    public void setCanonEq(final boolean canonEq) {
        this.canonEq = canonEq;
    }
    
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public boolean isUnicodeCase() {
        return this.unicodeCase;
    }
    
    public void setUnicodeCase(final boolean unicodeCase) {
        this.unicodeCase = unicodeCase;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
