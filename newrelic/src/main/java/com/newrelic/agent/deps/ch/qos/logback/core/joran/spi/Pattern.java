// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class Pattern
{
    ArrayList<String> partList;
    
    public Pattern() {
        this.partList = new ArrayList<String>();
    }
    
    public Pattern(final List<String> list) {
        (this.partList = new ArrayList<String>()).addAll(list);
    }
    
    public Pattern(final String p) {
        this();
        if (p == null) {
            return;
        }
        int lastIndex = 0;
        while (true) {
            final int k = p.indexOf(47, lastIndex);
            if (k == -1) {
                break;
            }
            final String c = p.substring(lastIndex, k);
            if (c.length() > 0) {
                this.partList.add(c);
            }
            lastIndex = k + 1;
        }
        final String lastPart = p.substring(lastIndex);
        if (lastPart != null && lastPart.length() > 0) {
            this.partList.add(p.substring(lastIndex));
        }
    }
    
    public List<String> getCopyOfPartList() {
        return new ArrayList<String>(this.partList);
    }
    
    public Object clone() {
        final Pattern p = new Pattern();
        p.partList.addAll(this.partList);
        return p;
    }
    
    public void push(final String s) {
        this.partList.add(s);
    }
    
    public int size() {
        return this.partList.size();
    }
    
    public String get(final int i) {
        return this.partList.get(i);
    }
    
    public void pop() {
        if (!this.partList.isEmpty()) {
            this.partList.remove(this.partList.size() - 1);
        }
    }
    
    public String peekLast() {
        if (!this.partList.isEmpty()) {
            final int size = this.partList.size();
            return this.partList.get(size - 1);
        }
        return null;
    }
    
    public int getTailMatchLength(final Pattern p) {
        if (p == null) {
            return 0;
        }
        final int lSize = this.partList.size();
        final int rSize = p.partList.size();
        if (lSize == 0 || rSize == 0) {
            return 0;
        }
        final int minLen = (lSize <= rSize) ? lSize : rSize;
        int match = 0;
        for (int i = 1; i <= minLen; ++i) {
            final String l = this.partList.get(lSize - i);
            final String r = p.partList.get(rSize - i);
            if (!this.equalityCheck(l, r)) {
                break;
            }
            ++match;
        }
        return match;
    }
    
    public boolean isContained(final Pattern p) {
        if (p == null) {
            return false;
        }
        final String lStr = this.toString();
        return lStr.contains(p.toString());
    }
    
    public int getPrefixMatchLength(final Pattern p) {
        if (p == null) {
            return 0;
        }
        final int lSize = this.partList.size();
        final int rSize = p.partList.size();
        if (lSize == 0 || rSize == 0) {
            return 0;
        }
        final int minLen = (lSize <= rSize) ? lSize : rSize;
        int match = 0;
        for (int i = 0; i < minLen; ++i) {
            final String l = this.partList.get(i);
            final String r = p.partList.get(i);
            if (!this.equalityCheck(l, r)) {
                break;
            }
            ++match;
        }
        return match;
    }
    
    private boolean equalityCheck(final String x, final String y) {
        return x.equalsIgnoreCase(y);
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Pattern)) {
            return false;
        }
        final Pattern r = (Pattern)o;
        if (r.size() != this.size()) {
            return false;
        }
        for (int len = this.size(), i = 0; i < len; ++i) {
            if (!this.equalityCheck(this.get(i), r.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public int hashCode() {
        int hc = 0;
        for (int len = this.size(), i = 0; i < len; ++i) {
            hc ^= this.get(i).toLowerCase().hashCode();
        }
        return hc;
    }
    
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (final String current : this.partList) {
            result.append("[").append(current).append("]");
        }
        return result.toString();
    }
}
