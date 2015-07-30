// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.database;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class SqlObfuscator
{
    public static final String OBFUSCATED_SETTING = "obfuscated";
    public static final String RAW_SETTING = "raw";
    public static final String OFF_SETTING = "off";
    
    public abstract String obfuscateSql(final String p0);
    
    public boolean isObfuscating() {
        return false;
    }
    
    public static SqlObfuscator getDefaultSqlObfuscator() {
        return new DefaultSqlObfuscator();
    }
    
    static SqlObfuscator getNoObfuscationSqlObfuscator() {
        return new SqlObfuscator() {
            public String obfuscateSql(final String sql) {
                return sql;
            }
        };
    }
    
    static SqlObfuscator getNoSqlObfuscator() {
        return new SqlObfuscator() {
            public String obfuscateSql(final String sql) {
                return null;
            }
        };
    }
    
    public static SqlObfuscator getCachingSqlObfuscator(final SqlObfuscator sqlObfuscator) {
        if (sqlObfuscator.isObfuscating()) {
            return new CachingSqlObfuscator(sqlObfuscator);
        }
        return sqlObfuscator;
    }
    
    static class DefaultSqlObfuscator extends SqlObfuscator
    {
        protected static final Pattern[] OBFUSCATION_PATTERNS;
        private static final Pattern DIGIT_PATTERN;
        
        DefaultSqlObfuscator() {
            super(null);
        }
        
        public String obfuscateSql(String sql) {
            if (sql == null || sql.length() == 0) {
                return sql;
            }
            for (final Pattern pattern : DefaultSqlObfuscator.OBFUSCATION_PATTERNS) {
                sql = pattern.matcher(sql).replaceAll("?");
            }
            return sql;
        }
        
        public boolean isObfuscating() {
            return true;
        }
        
        static {
            DIGIT_PATTERN = Pattern.compile("(?<=[-+*/,_<=>)(\\.\\s])\\d+(?=[-+*/,_<=>)(\\.\\s]|$)");
            OBFUSCATION_PATTERNS = new Pattern[] { Pattern.compile("'(.*?[^\\'])??'(?!')", 32), Pattern.compile("\"(.*?[^\\\"])??\"(?!\")", 32), DefaultSqlObfuscator.DIGIT_PATTERN };
        }
    }
    
    static class CachingSqlObfuscator extends SqlObfuscator
    {
        private final Map<String, String> cache;
        private final SqlObfuscator sqlObfuscator;
        
        public CachingSqlObfuscator(final SqlObfuscator sqlObfuscator) {
            super(null);
            this.cache = new HashMap<String, String>();
            this.sqlObfuscator = sqlObfuscator;
        }
        
        public String obfuscateSql(final String sql) {
            String obfuscatedSql = this.cache.get(sql);
            if (obfuscatedSql == null) {
                obfuscatedSql = this.sqlObfuscator.obfuscateSql(sql);
                this.cache.put(sql, obfuscatedSql);
            }
            return obfuscatedSql;
        }
        
        public boolean isObfuscating() {
            return this.sqlObfuscator.isObfuscating();
        }
    }
}
