// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.bridge.Transaction;

public interface Variables
{
    Object loadThis(int p0);
    
    Transaction loadCurrentTransaction();
    
     <N extends Number> N loadLocal(int p0, Type p1, N p2);
    
     <N extends Number> N load(N p0, Runnable p1);
    
     <O> O load(Class<O> p0, Runnable p1);
    
     <O> O loadLocal(int p0, Class<O> p1);
}
