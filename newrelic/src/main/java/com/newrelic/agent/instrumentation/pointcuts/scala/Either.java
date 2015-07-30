// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.instrumentation.pointcuts.LoadOnBootstrap;

@LoadOnBootstrap
public interface Either
{
    Object get();
}
