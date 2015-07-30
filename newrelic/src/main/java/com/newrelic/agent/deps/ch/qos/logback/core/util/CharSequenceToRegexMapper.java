// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

class CharSequenceToRegexMapper
{
    String toRegex(final CharSequenceState css) {
        final int occurrences = css.occurrences;
        final char c = css.c;
        switch (css.c) {
            case 'G':
            case 'z': {
                return ".*";
            }
            case 'M': {
                if (occurrences >= 3) {
                    return ".{3,12}";
                }
                return this.number(occurrences);
            }
            case 'D':
            case 'F':
            case 'H':
            case 'K':
            case 'S':
            case 'W':
            case 'd':
            case 'h':
            case 'k':
            case 'm':
            case 's':
            case 'w':
            case 'y': {
                return this.number(occurrences);
            }
            case 'E': {
                return ".{2,12}";
            }
            case 'a': {
                return ".{2}";
            }
            case 'Z': {
                return "(\\+|-)\\d{4}";
            }
            case '.': {
                return "\\.";
            }
            case '\\': {
                throw new IllegalStateException("Forward slashes are not allowed");
            }
            case '\'': {
                if (occurrences == 1) {
                    return "";
                }
                throw new IllegalStateException("Too many single quotes");
            }
            default: {
                if (occurrences == 1) {
                    return "" + c;
                }
                return c + "{" + occurrences + "}";
            }
        }
    }
    
    private String number(final int occurrences) {
        return "\\d{" + occurrences + "}";
    }
}
