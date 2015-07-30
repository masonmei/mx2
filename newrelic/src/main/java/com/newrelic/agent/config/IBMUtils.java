// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IBMUtils
{
    private static final Pattern srNumberPattern;
    
    public static boolean getIbmWorkaroundDefault() {
        try {
            final String jvmVendor = System.getProperty("java.vendor");
            if ("IBM Corporation".equals(jvmVendor)) {
                final String jvmVersion = System.getProperty("java.specification.version", "");
                final int srNum = getIbmSRNumber();
                return (!"1.6".equals(jvmVersion) || srNum < 4) && (!"1.7".equals(jvmVersion) || srNum < 4);
            }
            return false;
        }
        catch (Exception e) {
            return true;
        }
    }
    
    public static int getIbmSRNumber() {
        if ("IBM Corporation".equals(System.getProperty("java.vendor"))) {
            final String runtimeVersion = System.getProperty("java.runtime.version", "");
            final Matcher matcher = IBMUtils.srNumberPattern.matcher(runtimeVersion);
            if (matcher.find()) {
                return Integer.valueOf(matcher.group(1));
            }
        }
        return -1;
    }
    
    static {
        srNumberPattern = Pattern.compile("\\(SR([0-9]+)[^()]*\\)\\s*$");
    }
}
