// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.SortedSet;

interface SortedMultisetBridge<E> extends Multiset<E>
{
    SortedSet<E> elementSet();
}
