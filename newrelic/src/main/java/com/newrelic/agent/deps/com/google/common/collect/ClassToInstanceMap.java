// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public interface ClassToInstanceMap<B> extends Map<Class<? extends B>, B>
{
     <T extends B> T getInstance(Class<T> p0);
    
     <T extends B> T putInstance(Class<T> p0, @Nullable T p1);
}
