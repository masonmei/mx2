// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.hash;

import java.nio.charset.Charset;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface Hasher extends PrimitiveSink
{
    Hasher putByte(byte p0);
    
    Hasher putBytes(byte[] p0);
    
    Hasher putBytes(byte[] p0, int p1, int p2);
    
    Hasher putShort(short p0);
    
    Hasher putInt(int p0);
    
    Hasher putLong(long p0);
    
    Hasher putFloat(float p0);
    
    Hasher putDouble(double p0);
    
    Hasher putBoolean(boolean p0);
    
    Hasher putChar(char p0);
    
    Hasher putUnencodedChars(CharSequence p0);
    
    Hasher putString(CharSequence p0, Charset p1);
    
     <T> Hasher putObject(T p0, Funnel<? super T> p1);
    
    HashCode hash();
}
