// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.reflect;

import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import java.util.Map;

@Beta
public interface TypeToInstanceMap<B> extends Map<TypeToken<? extends B>, B>
{
    @Nullable
     <T extends B> T getInstance(Class<T> p0);
    
    @Nullable
     <T extends B> T putInstance(Class<T> p0, @Nullable T p1);
    
    @Nullable
     <T extends B> T getInstance(TypeToken<T> p0);
    
    @Nullable
     <T extends B> T putInstance(TypeToken<T> p0, @Nullable T p1);
}
