// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

class Util
{
    static String stripLeadingHyphens(final String str) {
        if (str == null) {
            return null;
        }
        if (str.startsWith("--")) {
            return str.substring(2, str.length());
        }
        if (str.startsWith("-")) {
            return str.substring(1, str.length());
        }
        return str;
    }
    
    static String stripLeadingAndTrailingQuotes(String str) {
        if (str.startsWith("\"")) {
            str = str.substring(1, str.length());
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
