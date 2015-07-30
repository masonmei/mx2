// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.io.File;

public class FileFilterUtil
{
    public static void sortFileArrayByName(final File[] fileArray) {
        Arrays.sort(fileArray, new Comparator<File>() {
            public int compare(final File o1, final File o2) {
                final String o1Name = o1.getName();
                final String o2Name = o2.getName();
                return o1Name.compareTo(o2Name);
            }
        });
    }
    
    public static void reverseSortFileArrayByName(final File[] fileArray) {
        Arrays.sort(fileArray, new Comparator<File>() {
            public int compare(final File o1, final File o2) {
                final String o1Name = o1.getName();
                final String o2Name = o2.getName();
                return o2Name.compareTo(o1Name);
            }
        });
    }
    
    public static String afterLastSlash(final String sregex) {
        final int i = sregex.lastIndexOf(47);
        if (i == -1) {
            return sregex;
        }
        return sregex.substring(i + 1);
    }
    
    public static boolean isEmptyDirectory(final File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("[" + dir + "] must be a directory");
        }
        final String[] filesInDir = dir.list();
        return filesInDir == null || filesInDir.length == 0;
    }
    
    public static File[] filesInFolderMatchingStemRegex(final File file, final String stemRegex) {
        if (file == null) {
            return new File[0];
        }
        if (!file.exists() || !file.isDirectory()) {
            return new File[0];
        }
        return file.listFiles(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return name.matches(stemRegex);
            }
        });
    }
    
    public static int findHighestCounter(final File[] matchingFileArray, final String stemRegex) {
        int max = Integer.MIN_VALUE;
        for (final File aFile : matchingFileArray) {
            final int aCounter = extractCounter(aFile, stemRegex);
            if (max < aCounter) {
                max = aCounter;
            }
        }
        return max;
    }
    
    public static int extractCounter(final File file, final String stemRegex) {
        final Pattern p = Pattern.compile(stemRegex);
        final String lastFileName = file.getName();
        final Matcher m = p.matcher(lastFileName);
        if (!m.matches()) {
            throw new IllegalStateException("The regex [" + stemRegex + "] should match [" + lastFileName + "]");
        }
        final String counterAsStr = m.group(1);
        return new Integer(counterAsStr);
    }
    
    public static String slashify(final String in) {
        return in.replace('\\', '/');
    }
    
    public static void removeEmptyParentDirectories(final File file, final int recursivityCount) {
        if (recursivityCount >= 3) {
            return;
        }
        final File parent = file.getParentFile();
        if (parent.isDirectory() && isEmptyDirectory(parent)) {
            parent.delete();
            removeEmptyParentDirectories(parent, recursivityCount + 1);
        }
    }
}
