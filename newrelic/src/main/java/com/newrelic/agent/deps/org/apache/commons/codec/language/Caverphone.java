// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.language;

import com.newrelic.agent.deps.org.apache.commons.codec.EncoderException;
import com.newrelic.agent.deps.org.apache.commons.codec.StringEncoder;

public class Caverphone implements StringEncoder
{
    private final Caverphone2 encoder;
    
    public Caverphone() {
        this.encoder = new Caverphone2();
    }
    
    public String caverphone(final String source) {
        return this.encoder.encode(source);
    }
    
    public Object encode(final Object pObject) throws EncoderException {
        if (!(pObject instanceof String)) {
            throw new EncoderException("Parameter supplied to Caverphone encode is not of type java.lang.String");
        }
        return this.caverphone((String)pObject);
    }
    
    public String encode(final String pString) {
        return this.caverphone(pString);
    }
    
    public boolean isCaverphoneEqual(final String str1, final String str2) {
        return this.caverphone(str1).equals(this.caverphone(str2));
    }
}
