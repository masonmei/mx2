// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.io;

import java.io.DataInput;

public interface ByteArrayDataInput extends DataInput
{
    void readFully(byte[] p0);
    
    void readFully(byte[] p0, int p1, int p2);
    
    int skipBytes(int p0);
    
    boolean readBoolean();
    
    byte readByte();
    
    int readUnsignedByte();
    
    short readShort();
    
    int readUnsignedShort();
    
    char readChar();
    
    int readInt();
    
    long readLong();
    
    float readFloat();
    
    double readDouble();
    
    String readLine();
    
    String readUTF();
}
