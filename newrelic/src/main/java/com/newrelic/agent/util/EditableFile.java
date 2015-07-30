// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.File;

public class EditableFile
{
    String filePath;
    String fileAsString;
    public final String comment;
    static final String lineSep;
    
    public EditableFile(final String filestr) throws NullPointerException, FileNotFoundException, IOException {
        if (filestr == null || filestr.equals("")) {
            throw new NullPointerException("A null or empty string can't become an EditableFile.");
        }
        this.filePath = filestr;
        final File file = new File(this.filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + this.filePath + " does not exist, so it can't become an EditableFile.");
        }
        final StringBuilder sb = new StringBuilder();
        try {
            final BufferedReader in = new BufferedReader(new FileReader(this.filePath));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str + EditableFile.lineSep);
            }
            in.close();
        }
        catch (FileNotFoundException fnfe) {
            fnfe.getMessage();
            fnfe.printStackTrace();
            throw new FileNotFoundException("Couldn't create EditableFile due to FileNotFoundException");
        }
        catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
            throw new IOException("Couldn't create EditableFile due to IOException");
        }
        this.fileAsString = sb.toString();
        if (this.filePath.endsWith(".bat")) {
            this.comment = "::";
        }
        else if (this.filePath.endsWith(".java")) {
            this.comment = "//";
        }
        else {
            this.comment = "#";
        }
    }
    
    public String getContents() {
        return this.fileAsString;
    }
    
    public String getLocation() {
        return this.filePath;
    }
    
    public boolean contains(final String regex) {
        if (this.fileAsString != null) {
            final Pattern p = Pattern.compile(regex, 8);
            final Matcher m = p.matcher(this.fileAsString);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }
    
    public String replaceFirst(final String regex, final String replacement) {
        return this.replaceFirst(regex, replacement, true);
    }
    
    public String replaceFirst(final String regex, final String replacement, final boolean isMultiLine) {
        Pattern p;
        if (isMultiLine) {
            p = Pattern.compile(regex, 8);
        }
        else {
            p = Pattern.compile(regex);
        }
        final Matcher m = p.matcher(this.fileAsString);
        this.fileAsString = m.replaceFirst(replacement);
        this.write();
        return this.fileAsString;
    }
    
    public String replaceAll(final String regex, final String replacement) {
        return this.replaceAll(regex, replacement, true);
    }
    
    public String replaceAll(final String regex, final String replacement, final boolean isMultiLine) {
        Pattern p;
        if (isMultiLine) {
            p = Pattern.compile(regex, 8);
        }
        else {
            p = Pattern.compile(regex);
        }
        final Matcher m = p.matcher(this.fileAsString);
        this.fileAsString = m.replaceAll(replacement);
        this.write();
        return this.fileAsString;
    }
    
    public String insertBeforeLocator(final String regex, final String textToInsert, final boolean isMultiLine) {
        this.fileAsString = this.replaceFirst("(" + regex + ")", textToInsert + EditableFile.lineSep + "$1", isMultiLine);
        this.write();
        return this.fileAsString;
    }
    
    public String insertAfterLocator(final String regex, final String textToInsert, final boolean isMultiLine) {
        this.fileAsString = this.replaceFirst("(" + regex + ")", "$1" + EditableFile.lineSep + textToInsert, isMultiLine);
        this.write();
        return this.fileAsString;
    }
    
    public void commentOutFirstLineMatching(final String regex) {
        this.replaceFirst("(" + regex + ")", this.comment + "$1");
    }
    
    public void commentOutAllLinesMatching(final String regex) {
        this.replaceAll("(" + regex + ")", this.comment + "$1");
    }
    
    public void append(final String text) {
        this.fileAsString = this.fileAsString + EditableFile.lineSep + text;
        this.write();
    }
    
    public String backup() {
        final DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String filename = this.filePath + "." + df.format(new Date());
        if (this.write(filename)) {
            return filename;
        }
        return "";
    }
    
    private boolean write() {
        return this.write(this.filePath);
    }
    
    private boolean write(final String pathToFile) {
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter(pathToFile));
            out.write(this.fileAsString);
            out.close();
            return true;
        }
        catch (IOException e) {
            System.out.println("Problem writing file to disk");
            e.getMessage();
            e.printStackTrace();
            return false;
        }
    }
    
    static {
        lineSep = System.getProperty("line.separator");
    }
}
