// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.reader;

import java.nio.charset.Charset;
import java.io.IOException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.regex.Matcher;
import java.io.Reader;
import java.util.regex.Pattern;

public class Reader
{
    static final Pattern NON_PRINTABLE;
    private static final String LINEBR = "\n\u0085\u2028\u2029";
    private String name;
    private final java.io.Reader stream;
    private int pointer;
    private boolean eof;
    private final StringBuffer buffer;
    private int index;
    private int line;
    private int column;
    
    public Reader(final String stream) {
        this.pointer = 0;
        this.eof = true;
        this.index = 0;
        this.line = 0;
        this.column = 0;
        this.name = "<string>";
        this.buffer = new StringBuffer();
        this.checkPrintable(stream);
        this.buffer.append(stream);
        this.stream = null;
        this.eof = true;
    }
    
    public Reader(final java.io.Reader reader) {
        this.pointer = 0;
        this.eof = true;
        this.index = 0;
        this.line = 0;
        this.column = 0;
        this.name = "<reader>";
        this.buffer = new StringBuffer();
        this.stream = reader;
        this.eof = false;
    }
    
    void checkPrintable(final CharSequence data) {
        final Matcher em = Reader.NON_PRINTABLE.matcher(data);
        if (em.find()) {
            final int position = this.index + this.buffer.length() - this.pointer + em.start();
            throw new ReaderException(this.name, position, em.group().charAt(0), " special characters are not allowed");
        }
    }
    
    public Mark getMark() {
        if (this.stream == null) {
            return new Mark(this.name, this.index, this.line, this.column, this.buffer.toString(), this.pointer);
        }
        return new Mark(this.name, this.index, this.line, this.column, null, 0);
    }
    
    public void forward() {
        this.forward(1);
    }
    
    public void forward(final int length) {
        if (this.pointer + length + 1 >= this.buffer.length()) {
            this.update(length + 1);
        }
        char ch = '\0';
        for (int i = 0; i < length; ++i) {
            ch = this.buffer.charAt(this.pointer);
            ++this.pointer;
            ++this.index;
            if ("\n\u0085\u2028\u2029".indexOf(ch) != -1 || (ch == '\r' && this.buffer.charAt(this.pointer) != '\n')) {
                ++this.line;
                this.column = 0;
            }
            else if (ch != '\ufeff') {
                ++this.column;
            }
        }
    }
    
    public char peek() {
        return this.peek(0);
    }
    
    public char peek(final int index) {
        if (this.pointer + index + 1 > this.buffer.length()) {
            this.update(index + 1);
        }
        return this.buffer.charAt(this.pointer + index);
    }
    
    public String prefix(final int length) {
        if (this.pointer + length >= this.buffer.length()) {
            this.update(length);
        }
        if (this.pointer + length > this.buffer.length()) {
            return this.buffer.substring(this.pointer, this.buffer.length());
        }
        return this.buffer.substring(this.pointer, this.pointer + length);
    }
    
    private void update(final int length) {
        this.buffer.delete(0, this.pointer);
        this.pointer = 0;
        while (this.buffer.length() < length) {
            String rawData = "";
            if (!this.eof) {
                final char[] data = new char[1024];
                int converted = -2;
                try {
                    converted = this.stream.read(data);
                }
                catch (IOException ioe) {
                    throw new YAMLException(ioe);
                }
                if (converted == -1) {
                    this.eof = true;
                }
                else {
                    rawData = new String(data, 0, converted);
                }
            }
            this.checkPrintable(rawData);
            this.buffer.append(rawData);
            if (this.eof) {
                this.buffer.append('\0');
                break;
            }
        }
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public Charset getEncoding() {
        return Charset.forName(((UnicodeReader)this.stream).getEncoding());
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public int getLine() {
        return this.line;
    }
    
    static {
        NON_PRINTABLE = Pattern.compile("[^\t\n\r -~\u0085 -\ud7ff\ue000-\ufffc]");
    }
}
