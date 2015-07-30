// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.util.Iterator;

public interface HeaderElementIterator extends Iterator<Object>
{
    boolean hasNext();
    
    HeaderElement nextElement();
}
