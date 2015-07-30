// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import java.util.Random;

public class TransactionGuidFactory
{
    private static final ThreadLocal<Random> randomHolder;
    
    public static String generateGuid() {
        final char[] hexchars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        long random = TransactionGuidFactory.randomHolder.get().nextLong();
        final char[] result = new char[16];
        for (int i = 0; i < 16; ++i) {
            result[i] = hexchars[(int)(random & 0xFL)];
            random >>= 4;
        }
        return new String(result);
    }
    
    static {
        randomHolder = new ThreadLocal<Random>() {
            protected Random initialValue() {
                return new Random();
            }
        };
    }
}
