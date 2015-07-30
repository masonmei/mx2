// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.hash;

import java.nio.charset.Charset;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface HashFunction
{
    Hasher newHasher();
    
    Hasher newHasher(int p0);
    
    HashCode hashInt(int p0);
    
    HashCode hashLong(long p0);
    
    HashCode hashBytes(byte[] p0);
    
    HashCode hashBytes(byte[] p0, int p1, int p2);
    
    HashCode hashUnencodedChars(CharSequence p0);
    
    HashCode hashString(CharSequence p0, Charset p1);
    
     <T> HashCode hashObject(T p0, Funnel<? super T> p1);
    
    int bits();
}
