// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.language;

import com.newrelic.agent.deps.org.apache.commons.codec.EncoderException;
import com.newrelic.agent.deps.org.apache.commons.codec.StringEncoder;

public class RefinedSoundex implements StringEncoder
{
    public static final String US_ENGLISH_MAPPING_STRING = "01360240043788015936020505";
    private static final char[] US_ENGLISH_MAPPING;
    private final char[] soundexMapping;
    public static final RefinedSoundex US_ENGLISH;
    
    public RefinedSoundex() {
        this.soundexMapping = RefinedSoundex.US_ENGLISH_MAPPING;
    }
    
    public RefinedSoundex(final char[] mapping) {
        System.arraycopy(mapping, 0, this.soundexMapping = new char[mapping.length], 0, mapping.length);
    }
    
    public RefinedSoundex(final String mapping) {
        this.soundexMapping = mapping.toCharArray();
    }
    
    public int difference(final String s1, final String s2) throws EncoderException {
        return SoundexUtils.difference(this, s1, s2);
    }
    
    public Object encode(final Object pObject) throws EncoderException {
        if (!(pObject instanceof String)) {
            throw new EncoderException("Parameter supplied to RefinedSoundex encode is not of type java.lang.String");
        }
        return this.soundex((String)pObject);
    }
    
    public String encode(final String pString) {
        return this.soundex(pString);
    }
    
    char getMappingCode(final char c) {
        if (!Character.isLetter(c)) {
            return '\0';
        }
        return this.soundexMapping[Character.toUpperCase(c) - 'A'];
    }
    
    public String soundex(String str) {
        if (str == null) {
            return null;
        }
        str = SoundexUtils.clean(str);
        if (str.length() == 0) {
            return str;
        }
        final StringBuffer sBuf = new StringBuffer();
        sBuf.append(str.charAt(0));
        char last = '*';
        for (int i = 0; i < str.length(); ++i) {
            final char current = this.getMappingCode(str.charAt(i));
            if (current != last) {
                if (current != '\0') {
                    sBuf.append(current);
                }
                last = current;
            }
        }
        return sBuf.toString();
    }
    
    static {
        US_ENGLISH_MAPPING = "01360240043788015936020505".toCharArray();
        US_ENGLISH = new RefinedSoundex();
    }
}
