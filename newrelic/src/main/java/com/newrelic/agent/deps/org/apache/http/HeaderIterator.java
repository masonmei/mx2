// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.util.Iterator;

public interface HeaderIterator extends Iterator<Object>
{
    boolean hasNext();
    
    Header nextHeader();
}
