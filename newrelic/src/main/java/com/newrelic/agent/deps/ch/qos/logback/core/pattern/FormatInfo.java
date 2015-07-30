// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

public class FormatInfo
{
    private int min;
    private int max;
    private boolean leftPad;
    private boolean leftTruncate;
    
    public FormatInfo() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.leftPad = true;
        this.leftTruncate = true;
    }
    
    public FormatInfo(final int min, final int max) {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.leftPad = true;
        this.leftTruncate = true;
        this.min = min;
        this.max = max;
    }
    
    public FormatInfo(final int min, final int max, final boolean leftPad, final boolean leftTruncate) {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.leftPad = true;
        this.leftTruncate = true;
        this.min = min;
        this.max = max;
        this.leftPad = leftPad;
        this.leftTruncate = leftTruncate;
    }
    
    public static FormatInfo valueOf(final String str) throws IllegalArgumentException {
        if (str == null) {
            throw new NullPointerException("Argument cannot be null");
        }
        final FormatInfo fi = new FormatInfo();
        final int indexOfDot = str.indexOf(46);
        String minPart = null;
        String maxPart = null;
        if (indexOfDot != -1) {
            minPart = str.substring(0, indexOfDot);
            if (indexOfDot + 1 == str.length()) {
                throw new IllegalArgumentException("Formatting string [" + str + "] should not end with '.'");
            }
            maxPart = str.substring(indexOfDot + 1);
        }
        else {
            minPart = str;
        }
        if (minPart != null && minPart.length() > 0) {
            final int min = Integer.parseInt(minPart);
            if (min >= 0) {
                fi.min = min;
            }
            else {
                fi.min = -min;
                fi.leftPad = false;
            }
        }
        if (maxPart != null && maxPart.length() > 0) {
            final int max = Integer.parseInt(maxPart);
            if (max >= 0) {
                fi.max = max;
            }
            else {
                fi.max = -max;
                fi.leftTruncate = false;
            }
        }
        return fi;
    }
    
    public boolean isLeftPad() {
        return this.leftPad;
    }
    
    public void setLeftPad(final boolean leftAlign) {
        this.leftPad = leftAlign;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public void setMax(final int max) {
        this.max = max;
    }
    
    public int getMin() {
        return this.min;
    }
    
    public void setMin(final int min) {
        this.min = min;
    }
    
    public boolean isLeftTruncate() {
        return this.leftTruncate;
    }
    
    public void setLeftTruncate(final boolean leftTruncate) {
        this.leftTruncate = leftTruncate;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormatInfo)) {
            return false;
        }
        final FormatInfo r = (FormatInfo)o;
        return this.min == r.min && this.max == r.max && this.leftPad == r.leftPad && this.leftTruncate == r.leftTruncate;
    }
    
    public int hashCode() {
        int result = this.min;
        result = 31 * result + this.max;
        result = 31 * result + (this.leftPad ? 1 : 0);
        result = 31 * result + (this.leftTruncate ? 1 : 0);
        return result;
    }
    
    public String toString() {
        return "FormatInfo(" + this.min + ", " + this.max + ", " + this.leftPad + ", " + this.leftTruncate + ")";
    }
}
