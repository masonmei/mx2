// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.service.ServiceFactory;
import java.util.regex.Pattern;

public class ObfuscatorUtil
{
    private static final Pattern IN_CLAUSE_PATTERN;
    private static final String IN_CLAUSE_REPLACEMENT = "(?)";
    
    public static String obfuscateSql(final String sql) {
        final SqlObfuscator sqlObfuscator = ServiceFactory.getDatabaseService().getDefaultSqlObfuscator();
        return obfuscateInClauses(sqlObfuscator.obfuscateSql(sql));
    }
    
    private static String obfuscateInClauses(final String sql) {
        return ObfuscatorUtil.IN_CLAUSE_PATTERN.matcher(sql).replaceAll("(?)");
    }
    
    static {
        IN_CLAUSE_PATTERN = Pattern.compile("\\([?,\\s]*\\)");
    }
}
