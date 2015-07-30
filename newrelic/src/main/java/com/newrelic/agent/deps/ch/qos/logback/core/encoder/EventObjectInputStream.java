// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

public class EventObjectInputStream<E> extends InputStream
{
    NonClosableInputStream ncis;
    List<E> buffer;
    int index;
    
    EventObjectInputStream(final InputStream is) throws IOException {
        this.buffer = new ArrayList<E>();
        this.index = 0;
        this.ncis = new NonClosableInputStream(is);
    }
    
    public int read() throws IOException {
        throw new UnsupportedOperationException("Only the readEvent method is supported.");
    }
    
    public int available() throws IOException {
        return this.ncis.available();
    }
    
    public E readEvent() throws IOException {
        final E event = this.getFromBuffer();
        if (event != null) {
            return event;
        }
        this.internalReset();
        final int count = this.readHeader();
        if (count == -1) {
            return null;
        }
        this.readPayload(count);
        this.readFooter(count);
        return this.getFromBuffer();
    }
    
    private void internalReset() {
        this.index = 0;
        this.buffer.clear();
    }
    
    E getFromBuffer() {
        if (this.index >= this.buffer.size()) {
            return null;
        }
        return this.buffer.get(this.index++);
    }
    
    int readHeader() throws IOException {
        final byte[] headerBA = new byte[16];
        final int bytesRead = this.ncis.read(headerBA);
        if (bytesRead == -1) {
            return -1;
        }
        int offset = 0;
        final int startPebble = ByteArrayUtil.readInt(headerBA, offset);
        if (startPebble != 1853421169) {
            throw new IllegalStateException("Does not look like data created by ObjectStreamEncoder");
        }
        offset += 4;
        final int count = ByteArrayUtil.readInt(headerBA, offset);
        offset += 4;
        final int endPointer = ByteArrayUtil.readInt(headerBA, offset);
        offset += 4;
        final int checksum = ByteArrayUtil.readInt(headerBA, offset);
        if (checksum != (0x6E78F671 ^ count)) {
            throw new IllegalStateException("Invalid checksum");
        }
        return count;
    }
    
    E readEvents(final ObjectInputStream ois) throws IOException {
        E e = null;
        try {
            e = (E)ois.readObject();
            this.buffer.add(e);
        }
        catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        return e;
    }
    
    void readFooter(final int count) throws IOException {
        final byte[] headerBA = new byte[8];
        this.ncis.read(headerBA);
        int offset = 0;
        final int stopPebble = ByteArrayUtil.readInt(headerBA, offset);
        if (stopPebble != 640373619) {
            throw new IllegalStateException("Looks like a corrupt stream");
        }
        offset += 4;
        final int checksum = ByteArrayUtil.readInt(headerBA, offset);
        if (checksum != (0x262B5373 ^ count)) {
            throw new IllegalStateException("Invalid checksum");
        }
    }
    
    void readPayload(final int count) throws IOException {
        final List<E> eventList = new ArrayList<E>(count);
        final ObjectInputStream ois = new ObjectInputStream(this.ncis);
        for (int i = 0; i < count; ++i) {
            final E e = this.readEvents(ois);
            eventList.add(e);
        }
        ois.close();
    }
    
    public void close() throws IOException {
        this.ncis.realClose();
    }
}
