// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.cli;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Comparator;

public class HelpFormatter
{
    public static final int DEFAULT_WIDTH = 74;
    public static final int DEFAULT_LEFT_PAD = 1;
    public static final int DEFAULT_DESC_PAD = 3;
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";
    public static final String DEFAULT_OPT_PREFIX = "-";
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";
    public static final String DEFAULT_ARG_NAME = "arg";
    public int defaultWidth;
    public int defaultLeftPad;
    public int defaultDescPad;
    public String defaultSyntaxPrefix;
    public String defaultNewLine;
    public String defaultOptPrefix;
    public String defaultLongOptPrefix;
    public String defaultArgName;
    protected Comparator optionComparator;
    
    public HelpFormatter() {
        this.defaultWidth = 74;
        this.defaultLeftPad = 1;
        this.defaultDescPad = 3;
        this.defaultSyntaxPrefix = "usage: ";
        this.defaultNewLine = System.getProperty("line.separator");
        this.defaultOptPrefix = "-";
        this.defaultLongOptPrefix = "--";
        this.defaultArgName = "arg";
        this.optionComparator = new OptionComparator();
    }
    
    public void setWidth(final int width) {
        this.defaultWidth = width;
    }
    
    public int getWidth() {
        return this.defaultWidth;
    }
    
    public void setLeftPadding(final int padding) {
        this.defaultLeftPad = padding;
    }
    
    public int getLeftPadding() {
        return this.defaultLeftPad;
    }
    
    public void setDescPadding(final int padding) {
        this.defaultDescPad = padding;
    }
    
    public int getDescPadding() {
        return this.defaultDescPad;
    }
    
    public void setSyntaxPrefix(final String prefix) {
        this.defaultSyntaxPrefix = prefix;
    }
    
    public String getSyntaxPrefix() {
        return this.defaultSyntaxPrefix;
    }
    
    public void setNewLine(final String newline) {
        this.defaultNewLine = newline;
    }
    
    public String getNewLine() {
        return this.defaultNewLine;
    }
    
    public void setOptPrefix(final String prefix) {
        this.defaultOptPrefix = prefix;
    }
    
    public String getOptPrefix() {
        return this.defaultOptPrefix;
    }
    
    public void setLongOptPrefix(final String prefix) {
        this.defaultLongOptPrefix = prefix;
    }
    
    public String getLongOptPrefix() {
        return this.defaultLongOptPrefix;
    }
    
    public void setArgName(final String name) {
        this.defaultArgName = name;
    }
    
    public String getArgName() {
        return this.defaultArgName;
    }
    
    public Comparator getOptionComparator() {
        return this.optionComparator;
    }
    
    public void setOptionComparator(final Comparator comparator) {
        if (comparator == null) {
            this.optionComparator = new OptionComparator();
        }
        else {
            this.optionComparator = comparator;
        }
    }
    
    public void printHelp(final String cmdLineSyntax, final Options options) {
        this.printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, false);
    }
    
    public void printHelp(final String cmdLineSyntax, final Options options, final boolean autoUsage) {
        this.printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, autoUsage);
    }
    
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer) {
        this.printHelp(cmdLineSyntax, header, options, footer, false);
    }
    
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage) {
        this.printHelp(this.defaultWidth, cmdLineSyntax, header, options, footer, autoUsage);
    }
    
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer) {
        this.printHelp(width, cmdLineSyntax, header, options, footer, false);
    }
    
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage) {
        final PrintWriter pw = new PrintWriter(System.out);
        this.printHelp(pw, width, cmdLineSyntax, header, options, this.defaultLeftPad, this.defaultDescPad, footer, autoUsage);
        pw.flush();
    }
    
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad, final int descPad, final String footer) {
        this.printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
    }
    
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad, final int descPad, final String footer, final boolean autoUsage) {
        if (cmdLineSyntax == null || cmdLineSyntax.length() == 0) {
            throw new IllegalArgumentException("cmdLineSyntax not provided");
        }
        if (autoUsage) {
            this.printUsage(pw, width, cmdLineSyntax, options);
        }
        else {
            this.printUsage(pw, width, cmdLineSyntax);
        }
        if (header != null && header.trim().length() > 0) {
            this.printWrapped(pw, width, header);
        }
        this.printOptions(pw, width, options, leftPad, descPad);
        if (footer != null && footer.trim().length() > 0) {
            this.printWrapped(pw, width, footer);
        }
    }
    
    public void printUsage(final PrintWriter pw, final int width, final String app, final Options options) {
        final StringBuffer buff = new StringBuffer(this.defaultSyntaxPrefix).append(app).append(" ");
        final Collection processedGroups = new ArrayList();
        final List optList = new ArrayList(options.getOptions());
        Collections.sort((List<Object>)optList, this.getOptionComparator());
        final Iterator i = optList.iterator();
        while (i.hasNext()) {
            final Option option = i.next();
            final OptionGroup group = options.getOptionGroup(option);
            if (group != null) {
                if (!processedGroups.contains(group)) {
                    processedGroups.add(group);
                    this.appendOptionGroup(buff, group);
                }
            }
            else {
                appendOption(buff, option, option.isRequired());
            }
            if (i.hasNext()) {
                buff.append(" ");
            }
        }
        this.printWrapped(pw, width, buff.toString().indexOf(32) + 1, buff.toString());
    }
    
    private void appendOptionGroup(final StringBuffer buff, final OptionGroup group) {
        if (!group.isRequired()) {
            buff.append("[");
        }
        final List optList = new ArrayList(group.getOptions());
        Collections.sort((List<Object>)optList, this.getOptionComparator());
        final Iterator i = optList.iterator();
        while (i.hasNext()) {
            appendOption(buff, i.next(), true);
            if (i.hasNext()) {
                buff.append(" | ");
            }
        }
        if (!group.isRequired()) {
            buff.append("]");
        }
    }
    
    private static void appendOption(final StringBuffer buff, final Option option, final boolean required) {
        if (!required) {
            buff.append("[");
        }
        if (option.getOpt() != null) {
            buff.append("-").append(option.getOpt());
        }
        else {
            buff.append("--").append(option.getLongOpt());
        }
        if (option.hasArg() && option.hasArgName()) {
            buff.append(" <").append(option.getArgName()).append(">");
        }
        if (!required) {
            buff.append("]");
        }
    }
    
    public void printUsage(final PrintWriter pw, final int width, final String cmdLineSyntax) {
        final int argPos = cmdLineSyntax.indexOf(32) + 1;
        this.printWrapped(pw, width, this.defaultSyntaxPrefix.length() + argPos, this.defaultSyntaxPrefix + cmdLineSyntax);
    }
    
    public void printOptions(final PrintWriter pw, final int width, final Options options, final int leftPad, final int descPad) {
        final StringBuffer sb = new StringBuffer();
        this.renderOptions(sb, width, options, leftPad, descPad);
        pw.println(sb.toString());
    }
    
    public void printWrapped(final PrintWriter pw, final int width, final String text) {
        this.printWrapped(pw, width, 0, text);
    }
    
    public void printWrapped(final PrintWriter pw, final int width, final int nextLineTabStop, final String text) {
        final StringBuffer sb = new StringBuffer(text.length());
        this.renderWrappedText(sb, width, nextLineTabStop, text);
        pw.println(sb.toString());
    }
    
    protected StringBuffer renderOptions(final StringBuffer sb, final int width, final Options options, final int leftPad, final int descPad) {
        final String lpad = this.createPadding(leftPad);
        final String dpad = this.createPadding(descPad);
        int max = 0;
        final List prefixList = new ArrayList();
        final List optList = options.helpOptions();
        Collections.sort((List<Object>)optList, this.getOptionComparator());
        for (final Option option : optList) {
            final StringBuffer optBuf = new StringBuffer(8);
            if (option.getOpt() == null) {
                optBuf.append(lpad).append("   " + this.defaultLongOptPrefix).append(option.getLongOpt());
            }
            else {
                optBuf.append(lpad).append(this.defaultOptPrefix).append(option.getOpt());
                if (option.hasLongOpt()) {
                    optBuf.append(',').append(this.defaultLongOptPrefix).append(option.getLongOpt());
                }
            }
            if (option.hasArg()) {
                if (option.hasArgName()) {
                    optBuf.append(" <").append(option.getArgName()).append(">");
                }
                else {
                    optBuf.append(' ');
                }
            }
            prefixList.add(optBuf);
            max = ((optBuf.length() > max) ? optBuf.length() : max);
        }
        int x = 0;
        final Iterator j = optList.iterator();
        while (j.hasNext()) {
            final Option option2 = j.next();
            final StringBuffer optBuf = new StringBuffer(prefixList.get(x++).toString());
            if (optBuf.length() < max) {
                optBuf.append(this.createPadding(max - optBuf.length()));
            }
            optBuf.append(dpad);
            final int nextLineTabStop = max + descPad;
            if (option2.getDescription() != null) {
                optBuf.append(option2.getDescription());
            }
            this.renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());
            if (j.hasNext()) {
                sb.append(this.defaultNewLine);
            }
        }
        return sb;
    }
    
    protected StringBuffer renderWrappedText(final StringBuffer sb, final int width, int nextLineTabStop, String text) {
        int pos = this.findWrapPos(text, width, 0);
        if (pos == -1) {
            sb.append(this.rtrim(text));
            return sb;
        }
        sb.append(this.rtrim(text.substring(0, pos))).append(this.defaultNewLine);
        if (nextLineTabStop >= width) {
            nextLineTabStop = 1;
        }
        final String padding = this.createPadding(nextLineTabStop);
        while (true) {
            text = padding + text.substring(pos).trim();
            pos = this.findWrapPos(text, width, 0);
            if (pos == -1) {
                break;
            }
            if (text.length() > width && pos == nextLineTabStop - 1) {
                pos = width;
            }
            sb.append(this.rtrim(text.substring(0, pos))).append(this.defaultNewLine);
        }
        sb.append(text);
        return sb;
    }
    
    protected int findWrapPos(final String text, final int width, final int startPos) {
        int pos = -1;
        if (((pos = text.indexOf(10, startPos)) != -1 && pos <= width) || ((pos = text.indexOf(9, startPos)) != -1 && pos <= width)) {
            return pos + 1;
        }
        if (startPos + width >= text.length()) {
            return -1;
        }
        char c;
        for (pos = startPos + width; pos >= startPos && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r'; --pos) {}
        if (pos > startPos) {
            return pos;
        }
        for (pos = startPos + width; pos <= text.length() && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r'; ++pos) {}
        return (pos == text.length()) ? -1 : pos;
    }
    
    protected String createPadding(final int len) {
        final StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; ++i) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
    protected String rtrim(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        int pos;
        for (pos = s.length(); pos > 0 && Character.isWhitespace(s.charAt(pos - 1)); --pos) {}
        return s.substring(0, pos);
    }
    
    private static class OptionComparator implements Comparator
    {
        public int compare(final Object o1, final Object o2) {
            final Option opt1 = (Option)o1;
            final Option opt2 = (Option)o2;
            return opt1.getKey().compareToIgnoreCase(opt2.getKey());
        }
    }
}
