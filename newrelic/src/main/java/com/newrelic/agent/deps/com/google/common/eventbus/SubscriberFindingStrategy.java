// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.eventbus;

import com.newrelic.agent.deps.com.google.common.collect.Multimap;

interface SubscriberFindingStrategy
{
    Multimap<Class<?>, EventSubscriber> findAllSubscribers(Object p0);
}
