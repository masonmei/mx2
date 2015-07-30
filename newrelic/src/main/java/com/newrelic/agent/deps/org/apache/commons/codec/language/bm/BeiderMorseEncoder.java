// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.language.bm;

import com.newrelic.agent.deps.org.apache.commons.codec.EncoderException;
import com.newrelic.agent.deps.org.apache.commons.codec.StringEncoder;

public class BeiderMorseEncoder implements StringEncoder
{
    private PhoneticEngine engine;
    
    public BeiderMorseEncoder() {
        this.engine = new PhoneticEngine(NameType.GENERIC, RuleType.APPROX, true);
    }
    
    public Object encode(final Object source) throws EncoderException {
        if (!(source instanceof String)) {
            throw new EncoderException("BeiderMorseEncoder encode parameter is not of type String");
        }
        return this.encode((String)source);
    }
    
    public String encode(final String source) throws EncoderException {
        if (source == null) {
            return null;
        }
        return this.engine.encode(source);
    }
    
    public NameType getNameType() {
        return this.engine.getNameType();
    }
    
    public RuleType getRuleType() {
        return this.engine.getRuleType();
    }
    
    public boolean isConcat() {
        return this.engine.isConcat();
    }
    
    public void setConcat(final boolean concat) {
        this.engine = new PhoneticEngine(this.engine.getNameType(), this.engine.getRuleType(), concat);
    }
    
    public void setNameType(final NameType nameType) {
        this.engine = new PhoneticEngine(nameType, this.engine.getRuleType(), this.engine.isConcat());
    }
    
    public void setRuleType(final RuleType ruleType) {
        this.engine = new PhoneticEngine(this.engine.getNameType(), ruleType, this.engine.isConcat());
    }
}
