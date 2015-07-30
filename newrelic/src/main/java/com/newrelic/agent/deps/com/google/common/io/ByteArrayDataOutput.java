// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.io;

import java.io.DataOutput;

public interface ByteArrayDataOutput extends DataOutput
{
    void write(int p0);
    
    void write(byte[] p0);
    
    void write(byte[] p0, int p1, int p2);
    
    void writeBoolean(boolean p0);
    
    void writeByte(int p0);
    
    void writeShort(int p0);
    
    void writeChar(int p0);
    
    void writeInt(int p0);
    
    void writeLong(long p0);
    
    void writeFloat(float p0);
    
    void writeDouble(double p0);
    
    void writeChars(String p0);
    
    void writeUTF(String p0);
    
    @Deprecated
    void writeBytes(String p0);
    
    byte[] toByteArray();
}
