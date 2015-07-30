// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.util.Iterator;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectStreamEncoder<E> extends EncoderBase<E>
{
    public static final int START_PEBBLE = 1853421169;
    public static final int STOP_PEBBLE = 640373619;
    private int MAX_BUFFER_SIZE;
    List<E> bufferList;
    
    public ObjectStreamEncoder() {
        this.MAX_BUFFER_SIZE = 100;
        this.bufferList = new ArrayList<E>(this.MAX_BUFFER_SIZE);
    }
    
    public void doEncode(final E event) throws IOException {
        this.bufferList.add(event);
        if (this.bufferList.size() == this.MAX_BUFFER_SIZE) {
            this.writeBuffer();
        }
    }
    
    void writeHeader(final ByteArrayOutputStream baos, final int bufferSize) {
        ByteArrayUtil.writeInt(baos, 1853421169);
        ByteArrayUtil.writeInt(baos, bufferSize);
        ByteArrayUtil.writeInt(baos, 0);
        ByteArrayUtil.writeInt(baos, 0x6E78F671 ^ bufferSize);
    }
    
    void writeFooter(final ByteArrayOutputStream baos, final int bufferSize) {
        ByteArrayUtil.writeInt(baos, 640373619);
        ByteArrayUtil.writeInt(baos, 0x262B5373 ^ bufferSize);
    }
    
    void writeBuffer() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
        final int size = this.bufferList.size();
        this.writeHeader(baos, size);
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        for (final E e : this.bufferList) {
            oos.writeObject(e);
        }
        this.bufferList.clear();
        oos.flush();
        this.writeFooter(baos, size);
        final byte[] byteArray = baos.toByteArray();
        oos.close();
        this.writeEndPosition(byteArray);
        this.outputStream.write(byteArray);
    }
    
    void writeEndPosition(final byte[] byteArray) {
        final int offset = 8;
        ByteArrayUtil.writeInt(byteArray, offset, byteArray.length - offset);
    }
    
    public void init(final OutputStream os) throws IOException {
        super.init(os);
        this.bufferList.clear();
    }
    
    public void close() throws IOException {
        this.writeBuffer();
    }
}
