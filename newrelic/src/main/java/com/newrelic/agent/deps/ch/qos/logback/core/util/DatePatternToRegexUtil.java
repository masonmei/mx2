// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatePatternToRegexUtil
{
    final String datePattern;
    final int datePatternLength;
    final CharSequenceToRegexMapper regexMapper;
    
    public DatePatternToRegexUtil(final String datePattern) {
        this.regexMapper = new CharSequenceToRegexMapper();
        this.datePattern = datePattern;
        this.datePatternLength = datePattern.length();
    }
    
    public String toRegex() {
        final List<CharSequenceState> charSequenceList = this.tokenize();
        final StringBuilder sb = new StringBuilder();
        for (final CharSequenceState seq : charSequenceList) {
            sb.append(this.regexMapper.toRegex(seq));
        }
        return sb.toString();
    }
    
    private List<CharSequenceState> tokenize() {
        final List<CharSequenceState> sequenceList = new ArrayList<CharSequenceState>();
        CharSequenceState lastCharSequenceState = null;
        for (int i = 0; i < this.datePatternLength; ++i) {
            final char t = this.datePattern.charAt(i);
            if (lastCharSequenceState == null || lastCharSequenceState.c != t) {
                lastCharSequenceState = new CharSequenceState(t);
                sequenceList.add(lastCharSequenceState);
            }
            else {
                lastCharSequenceState.incrementOccurrences();
            }
        }
        return sequenceList;
    }
}
