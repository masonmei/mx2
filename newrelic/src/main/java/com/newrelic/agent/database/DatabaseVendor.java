// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.database;

import java.util.*;

import com.newrelic.agent.deps.org.json.simple.parser.ParseException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.json.simple.parser.JSONParser;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;

import java.sql.ResultSet;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import java.text.MessageFormat;
import java.sql.DatabaseMetaData;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.util.regex.Pattern;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public enum DatabaseVendor
{
    MYSQL("MySQL", "mysql", true, "^jdbc:mysql://([^/]*)/([^/\\?]*).*"), 
    ORACLE("Oracle", "oracle", false, "^jdbc:oracle:(thin|oci):(@//|@)([^:]*:\\d+)(/|:)(.*)") {
        protected String getHost(final Matcher matcher) {
            return matcher.group(3);
        }
        
        protected String getDatabase(final Matcher matcher) {
            return matcher.group(5);
        }
    }, 
    MICROSOFT("Microsoft SQL Server", "sqlserver", false, "^jdbc:sqlserver://([^;]*).*"), 
    POSTGRES("PostgreSQL", "postgresql", true, "^jdbc:postgresql://([^/]*)/([^\\?]*).*") {
        private final Set<String> EXPLAIN_WHITELIST;
        
        {
            this.EXPLAIN_WHITELIST = ImmutableSet.of("Plan", "Plans", "Node Type", "Relation Name", "Alias", "Startup Cost", "Total Cost", "Plan Rows", "Plan Width", "Parent Relationship", "Join Type", "Group Key", "Sort Key", "Relation Name", "Sort Method", "Sort Space Used", "Sort Space Type", "Scan Direction", "Index Name", "Actual Startup Time", "Actual Total Time", "Actual Rows", "Actual Loops", "Triggers", "Total Runtime", "Strategy");
        }
        
        public String getHost(final String url) {
            final Matcher matcher = DatabaseVendor.POSTGRES_URL_PATTERN.matcher(url);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            if (DatabaseVendor.POSTGRES_URL_PATTERN_DB.matcher(url).matches()) {
                return "localhost";
            }
            return super.getHost(url);
        }
        
        public String getExplainPlanSql(final String sql) throws SQLException {
            return "EXPLAIN (FORMAT JSON) " + sql;
        }
        
        public String getExplainPlanFormat() {
            return "json";
        }
        
        public Collection<Collection<Object>> parseExplainPlanResultSet(final int columnCount, final ResultSet rs, final RecordSql recordSql) throws SQLException {
            if (rs.next()) {
                final String json = rs.getObject(1).toString();
                try {
                    final JSONArray parse = (JSONArray)new JSONParser().parse(json);
                    if (RecordSql.obfuscated.equals(recordSql)) {
                        this.scrubPlan((Map<String, Object>) parse.get(0));
                    }
                    return Arrays.asList((Collection<Object>)parse);
                }
                catch (ParseException e) {
                    Agent.LOG.log(Level.FINER, "Unable to parse explain plan: {0}", new Object[] { e.toString() });
                    Collection<Object> strings = Collections.emptyList();
                    strings.add("Unable to parse explain plan");
                    return Arrays.asList(strings);
                }
            }
            Collection<Object> strings = Collections.emptyList();
            strings.add("No rows were returned by the explain plan");
            return Arrays.asList(strings);
        }
        
        private void scrubPlan(final Map<String, Object> plan) {
            final Map<String, Object> innerPlan = (Map<String, Object>) plan.get("Plan");
            if (innerPlan != null) {
                this.scrubPlan(innerPlan);
            }
            else {
                final JSONArray plans = (JSONArray) plan.get("Plans");
                if (plans != null) {
                    for (final Object childPlan : plans) {
                        this.scrubPlan((Map<String, Object>)childPlan);
                    }
                }
            }
            for (final Map.Entry<String, Object> entry : plan.entrySet()) {
                if (!this.EXPLAIN_WHITELIST.contains(entry.getKey())) {
                    entry.setValue("?");
                }
            }
        }
    }, 
    DB2("DB2", "db2", false, "jdbc:db2://server:port/database") {
        public String getHost(final String url) {
            String tmp = url.substring("jdbc:db2:".length());
            if (tmp.indexOf("//") != 0) {
                return "LocalHost";
            }
            tmp = tmp.substring(2);
            final int index = tmp.indexOf(47);
            return tmp.substring(0, index);
        }
    }, 
    DERBY("Apache Derby", "derby", false, "^$"), 
    UNKNOWN("Unknown", (String)null, false, "^$");
    
    private static final String UNKNOWN_STRING = "Unknown";
    private static final String REMOTE_SERVICE_DATABASE_METRIC_NAME = "RemoteService/Database/{0}/{1}/{2}/{3}/all";
    public static final MetricNameFormat UNKNOWN_DATABASE_METRIC_NAME;
    static final Pattern POSTGRES_URL_PATTERN;
    static final Pattern POSTGRES_URL_PATTERN_DB;
    private static final Pattern SIMPLE_DB_URL;
    private static final Pattern TYPE_PATTERN;
    private static final Map<String, DatabaseVendor> TYPE_TO_VENDOR;
    private final String name;
    final boolean explainPlanSupported;
    final Pattern urlPattern;
    final String type;
    
    private DatabaseVendor(final String name, final String type, final boolean explainSupported, final String urlPattern) {
        this.name = name;
        this.explainPlanSupported = explainSupported;
        this.type = type;
        this.urlPattern = Pattern.compile(urlPattern);
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isExplainPlanSupported() {
        return this.explainPlanSupported;
    }
    
    public String getExplainPlanSql(final String sql) throws SQLException {
        if (!this.isExplainPlanSupported()) {
            throw new SQLException("Unable to run explain plans for " + this.getName() + " databases");
        }
        return "EXPLAIN " + sql;
    }
    
    public static DatabaseVendor getDatabaseVendor(final String url) {
        final Matcher matcher = DatabaseVendor.TYPE_PATTERN.matcher(url);
        if (matcher.matches()) {
            final String type = matcher.group(1);
            if (type != null) {
                final DatabaseVendor vendor = DatabaseVendor.TYPE_TO_VENDOR.get(type);
                if (vendor != null) {
                    return vendor;
                }
            }
        }
        return DatabaseVendor.UNKNOWN;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getHost(final String url) {
        final Matcher matcher = this.urlPattern.matcher(url);
        if (matcher.matches()) {
            final String host = this.getHost(matcher);
            if (host != null) {
                return host;
            }
        }
        if (DatabaseVendor.SIMPLE_DB_URL.matcher(url).matches()) {
            return "localhost";
        }
        return "UnknownOrLocalhost";
    }
    
    protected String getHost(final Matcher matcher) {
        if (matcher.groupCount() >= 1) {
            return matcher.group(1);
        }
        return null;
    }
    
    public String getDatabase(final String url) {
        Matcher matcher = this.urlPattern.matcher(url);
        if (matcher.matches()) {
            final String db = this.getDatabase(matcher);
            if (db != null) {
                return db;
            }
        }
        matcher = DatabaseVendor.SIMPLE_DB_URL.matcher(url);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        return "Unknown";
    }
    
    protected String getDatabase(final Matcher matcher) {
        if (matcher.groupCount() >= 2) {
            return matcher.group(2);
        }
        return null;
    }
    
    public MetricNameFormat getDatabaseMetricName(final DatabaseMetaData metaData) {
        String databaseProductVersion = "Unknown";
        String databaseProductName = "Unknown";
        String host = "Unknown";
        String databaseName = "Unknown";
        if (metaData != null) {
            try {
                databaseProductVersion = metaData.getDatabaseProductVersion();
            }
            catch (Exception ex) {}
            try {
                databaseProductName = metaData.getDatabaseProductName();
            }
            catch (Exception ex2) {}
            try {
                final String url = metaData.getURL();
                host = this.getHost(url);
                databaseName = this.getDatabase(url);
            }
            catch (Exception ex3) {}
        }
        return new SimpleMetricNameFormat(MessageFormat.format("RemoteService/Database/{0}/{1}/{2}/{3}/all", databaseProductName, databaseProductVersion, host, databaseName));
    }
    
    public Collection<Collection<Object>> parseExplainPlanResultSet(final int columnCount, final ResultSet rs, final RecordSql recordSql) throws SQLException {
        final Collection<Collection<Object>> explains = new LinkedList<Collection<Object>>();
        while (rs.next()) {
            final Collection<Object> values = new LinkedList<Object>();
            for (int i = 1; i <= columnCount; ++i) {
                final Object obj = rs.getObject(i);
                values.add((obj == null) ? "" : obj.toString());
            }
            explains.add(values);
        }
        return explains;
    }
    
    public String getExplainPlanFormat() {
        return "text";
    }
    
    static {
        UNKNOWN_DATABASE_METRIC_NAME = new SimpleMetricNameFormat(MessageFormat.format("RemoteService/Database/{0}/{1}/{2}/{3}/all", "Unknown", "Unknown", "Unknown", "Unknown"));
        POSTGRES_URL_PATTERN = Pattern.compile("^jdbc:postgresql://([^/]*).*");
        POSTGRES_URL_PATTERN_DB = Pattern.compile("^jdbc:postgresql:(.*)");
        SIMPLE_DB_URL = Pattern.compile("jdbc:([^:]*):([^/;:].*)");
        TYPE_PATTERN = Pattern.compile("jdbc:([^:]*).*");
        TYPE_TO_VENDOR = new HashMap<String, DatabaseVendor>(7);
        for (final DatabaseVendor vendor : values()) {
            DatabaseVendor.TYPE_TO_VENDOR.put(vendor.getType(), vendor);
        }
    }
}
