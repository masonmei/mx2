// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transport;

import com.newrelic.org.apache.axis.encoding.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.IOException;
import com.newrelic.agent.deps.org.json.simple.JSONValue;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class DataSenderWriter extends OutputStreamWriter
{
    private static final int COMPRESSION_LEVEL = -1;
    
    protected DataSenderWriter(final OutputStream out) {
        super(out);
    }
    
    public static final String nullValue() {
        return "null";
    }
    
    public static final boolean isCompressingWriter(final Writer writer) {
        return !(writer instanceof DataSenderWriter);
    }
    
    public static final String getJsonifiedCompressedEncodedString(final Object data, final Writer writer) {
        return getJsonifiedCompressedEncodedString(data, writer, -1);
    }
    
    public static final String getJsonifiedCompressedEncodedString(final Object data, final Writer writer, final int compressionLevel) {
        if (writer instanceof DataSenderWriter) {
            return toJSONString(data);
        }
        return getJsonifiedCompressedEncodedString(data, compressionLevel);
    }
    
    public static final String toJSONString(final Object obj) {
        final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        final Writer writer = new DataSenderWriter(oStream);
        try {
            JSONValue.writeJSONString(obj, writer);
            writer.close();
            return oStream.toString();
        }
        catch (IOException e) {
            return JSONValue.toJSONString(obj);
        }
    }
    
    private static final String getJsonifiedCompressedEncodedString(final Object data, final int compressionLevel) {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final OutputStream zipStream = new DeflaterOutputStream(outStream, new Deflater(compressionLevel));
        final Writer out = new OutputStreamWriter(zipStream);
        try {
            JSONValue.writeJSONString(data, out);
            out.flush();
            out.close();
            outStream.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.encode(outStream.toByteArray());
    }
}
