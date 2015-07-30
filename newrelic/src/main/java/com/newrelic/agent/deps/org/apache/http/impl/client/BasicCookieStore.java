// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Comparator;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieIdentityComparator;
import com.newrelic.agent.deps.org.apache.http.annotation.GuardedBy;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import java.util.TreeSet;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import java.io.Serializable;
import com.newrelic.agent.deps.org.apache.http.client.CookieStore;

@ThreadSafe
public class BasicCookieStore implements CookieStore, Serializable
{
    private static final long serialVersionUID = -7581093305228232025L;
    @GuardedBy("this")
    private final TreeSet<Cookie> cookies;
    
    public BasicCookieStore() {
        this.cookies = new TreeSet<Cookie>(new CookieIdentityComparator());
    }
    
    public synchronized void addCookie(final Cookie cookie) {
        if (cookie != null) {
            this.cookies.remove(cookie);
            if (!cookie.isExpired(new Date())) {
                this.cookies.add(cookie);
            }
        }
    }
    
    public synchronized void addCookies(final Cookie[] cookies) {
        if (cookies != null) {
            for (final Cookie cooky : cookies) {
                this.addCookie(cooky);
            }
        }
    }
    
    public synchronized List<Cookie> getCookies() {
        return new ArrayList<Cookie>(this.cookies);
    }
    
    public synchronized boolean clearExpired(final Date date) {
        if (date == null) {
            return false;
        }
        boolean removed = false;
        final Iterator<Cookie> it = this.cookies.iterator();
        while (it.hasNext()) {
            if (it.next().isExpired(date)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }
    
    public synchronized void clear() {
        this.cookies.clear();
    }
    
    public synchronized String toString() {
        return this.cookies.toString();
    }
}
