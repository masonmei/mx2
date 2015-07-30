// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class Streams
{
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, 8192, false);
    }
    
    public static int copy(final InputStream input, final OutputStream output, final boolean closeStreams) throws IOException {
        return copy(input, output, 8192, closeStreams);
    }
    
    public static int copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        return copy(input, output, bufferSize, false);
    }
    
    public static int copy(final InputStream input, final OutputStream output, final int bufferSize, final boolean closeStreams) throws IOException {
        try {
            if (0 == bufferSize) {
                final boolean b = false;
                if (closeStreams) {
                    input.close();
                    output.close();
                }
                return b ? 1 : 0;
            }
            final byte[] buffer = new byte[bufferSize];
            int count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            final int n2 = count;
            if (closeStreams) {
                input.close();
                output.close();
            }
            return n2;
        }
        finally {
            if (closeStreams) {
                input.close();
                output.close();
            }
        }
    }
    
    public static byte[] read(final InputStream input, final boolean closeInputStream) throws IOException {
        return read(input, input.available(), closeInputStream);
    }
    
    public static byte[] read(final InputStream input, int expectedSize, final boolean closeInputStream) throws IOException {
        if (expectedSize <= 0) {
            expectedSize = 8192;
        }
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream(expectedSize);
        copy(input, outStream, expectedSize, closeInputStream);
        return outStream.toByteArray();
    }
}
