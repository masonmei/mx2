// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.hash;

import java.nio.charset.Charset;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface PrimitiveSink
{
    PrimitiveSink putByte(byte p0);
    
    PrimitiveSink putBytes(byte[] p0);
    
    PrimitiveSink putBytes(byte[] p0, int p1, int p2);
    
    PrimitiveSink putShort(short p0);
    
    PrimitiveSink putInt(int p0);
    
    PrimitiveSink putLong(long p0);
    
    PrimitiveSink putFloat(float p0);
    
    PrimitiveSink putDouble(double p0);
    
    PrimitiveSink putBoolean(boolean p0);
    
    PrimitiveSink putChar(char p0);
    
    PrimitiveSink putUnencodedChars(CharSequence p0);
    
    PrimitiveSink putString(CharSequence p0, Charset p1);
}
