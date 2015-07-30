// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.database;

import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.util.regex.Matcher;
import com.newrelic.agent.util.Strings;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Arrays;
import com.newrelic.agent.config.AgentConfig;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class DefaultDatabaseStatementParser implements DatabaseStatementParser
{
    private static final int PATTERN_SWITCHES = 34;
    private static final Pattern COMMENT_PATTERN;
    private static final Pattern NR_HINT_PATTERN;
    private static final Pattern VALID_METRIC_NAME_MATCHER;
    private static final Pattern FROM_MATCHER;
    private static final Pattern SELECT_PATTERN;
    private final Set<String> knownOperations;
    private final List<StatementFactory> statementFactories;
    private final boolean reportSqlParserErrors;
    private final StatementFactory selectStatementFactory;
    
    public DefaultDatabaseStatementParser(final AgentConfig agentConfig) {
        this.selectStatementFactory = new DefaultStatementFactory("select", DefaultDatabaseStatementParser.SELECT_PATTERN, true);
        this.reportSqlParserErrors = agentConfig.isReportSqlParserErrors();
        this.statementFactories = Arrays.asList(new InnerSelectStatementFactory(), new DefaultStatementFactory("show", Pattern.compile("^\\s*show\\s+(.*)$", 34), false) {
            protected boolean isValidModelName(final String name) {
                return true;
            }
        }, new DefaultStatementFactory("insert", Pattern.compile("^\\s*insert(?:\\s+ignore)?\\s+into\\s+([^\\s(,;]*).*", 34), true), new DefaultStatementFactory("update", Pattern.compile("^\\s*update\\s+([^\\s,;]*).*", 34), true), new DefaultStatementFactory("delete", Pattern.compile("^\\s*delete\\s+from\\s+([^\\s,(;]*).*", 34), true), new DDLStatementFactory("create", Pattern.compile("^\\s*create\\s+procedure.*", 34), "Procedure"), new SelectVariableStatementFactory(), new DDLStatementFactory("drop", Pattern.compile("^\\s*drop\\s+procedure.*", 34), "Procedure"), new DDLStatementFactory("create", Pattern.compile("^\\s*create\\s+table.*", 34), "Table"), new DDLStatementFactory("drop", Pattern.compile("^\\s*drop\\s+table.*", 34), "Table"), new DefaultStatementFactory("alter", Pattern.compile("^\\s*alter\\s+([^\\s]*).*", 34), false), new DefaultStatementFactory("call", Pattern.compile(".*call\\s+([^\\s(,]*).*", 34), true), new DefaultStatementFactory("exec", Pattern.compile(".*(?:exec|execute)\\s+([^\\s(,]*).*", 34), true), new DefaultStatementFactory("set", Pattern.compile("^\\s*set\\s+(.*)\\s*(as|=).*", 34), false));
        this.knownOperations = new HashSet<String>();
        for (final StatementFactory factory : this.statementFactories) {
            this.knownOperations.add(factory.getOperation());
        }
    }
    
    public ParsedDatabaseStatement getParsedDatabaseStatement(final String statement, final ResultSetMetaData metaData) {
        final Matcher hintMatcher = DefaultDatabaseStatementParser.NR_HINT_PATTERN.matcher(statement);
        if (hintMatcher.matches()) {
            final String model = hintMatcher.group(1).trim().toLowerCase();
            String operation = hintMatcher.group(2).toLowerCase();
            if (!this.knownOperations.contains(operation)) {
                operation = "unknown";
            }
            return new ParsedDatabaseStatement(model, operation, true);
        }
        if (metaData != null) {
            try {
                final int columnCount = metaData.getColumnCount();
                if (columnCount > 0) {
                    final String tableName = metaData.getTableName(1);
                    if (!Strings.isEmpty(tableName)) {
                        return new ParsedDatabaseStatement(tableName.toLowerCase(), "select", true);
                    }
                }
            }
            catch (Exception ex) {}
        }
        return this.parseStatement(statement);
    }
    
    ParsedDatabaseStatement parseStatement(String statement) {
        try {
            statement = DefaultDatabaseStatementParser.COMMENT_PATTERN.matcher(statement).replaceAll("");
            for (final StatementFactory factory : this.statementFactories) {
                final ParsedDatabaseStatement parsedStatement = factory.parseStatement(statement);
                if (parsedStatement != null) {
                    return parsedStatement;
                }
            }
            return DefaultDatabaseStatementParser.UNPARSEABLE_STATEMENT;
        }
        catch (Throwable t) {
            Agent.LOG.fine(MessageFormat.format("Unable to parse sql \"{0}\" - {1}", statement, t.toString()));
            Agent.LOG.log(Level.FINER, "SQL parsing error", t);
            return DefaultDatabaseStatementParser.UNPARSEABLE_STATEMENT;
        }
    }
    
    static boolean isValidName(final String string) {
        return DefaultDatabaseStatementParser.VALID_METRIC_NAME_MATCHER.matcher(string).matches();
    }
    
    static {
        COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", 32);
        NR_HINT_PATTERN = Pattern.compile("\\s*/\\*\\s*nrhint\\s*:\\s*([^\\*]*)\\s*\\*/\\s*([^\\s]*).*", 32);
        VALID_METRIC_NAME_MATCHER = Pattern.compile("[a-zA-z0-9.\\$]*");
        FROM_MATCHER = Pattern.compile("\\s+from\\s+", 34);
        SELECT_PATTERN = Pattern.compile("^\\s*select.*?\\sfrom[\\s\\[]+([^\\]\\s,)(;]*).*", 34);
    }
    
    class DefaultStatementFactory implements StatementFactory
    {
        private final Pattern pattern;
        protected final String key;
        private final boolean generateMetric;
        
        public DefaultStatementFactory(final String key, final Pattern pattern, final boolean generateMetric) {
            this.key = key;
            this.pattern = pattern;
            this.generateMetric = generateMetric;
        }
        
        protected boolean isMetricGenerator() {
            return this.generateMetric;
        }
        
        public ParsedDatabaseStatement parseStatement(final String statement) {
            final Matcher matcher = this.pattern.matcher(statement);
            if (!matcher.matches()) {
                return null;
            }
            String model = (matcher.groupCount() > 0) ? matcher.group(1).trim() : "unknown";
            if (model.length() == 0) {
                Agent.LOG.log(Level.FINE, MessageFormat.format("Parsed an empty model name for {0} statement : {1}", this.key, statement));
                return null;
            }
            model = Strings.unquoteDatabaseName(model);
            if (this.generateMetric && !this.isValidModelName(model)) {
                if (DefaultDatabaseStatementParser.this.reportSqlParserErrors) {
                    Agent.LOG.log(Level.FINE, MessageFormat.format("Parsed an invalid model name {0} for {1} statement : {2}", model, this.key, statement));
                }
                model = "ParseError";
            }
            return this.createParsedDatabaseStatement(model);
        }
        
        protected boolean isValidModelName(final String name) {
            return DefaultDatabaseStatementParser.isValidName(name);
        }
        
        ParsedDatabaseStatement createParsedDatabaseStatement(final String model) {
            return new ParsedDatabaseStatement(model.toLowerCase(), this.key, this.generateMetric);
        }
        
        public String getOperation() {
            return this.key;
        }
    }
    
    private class SelectVariableStatementFactory implements StatementFactory
    {
        private final ParsedDatabaseStatement innerSelectStatement;
        private final ParsedDatabaseStatement statement;
        private final Pattern pattern;
        
        private SelectVariableStatementFactory() {
            this.innerSelectStatement = new ParsedDatabaseStatement("INNER_SELECT", "select", false);
            this.statement = new ParsedDatabaseStatement("VARIABLE", "select", false);
            this.pattern = Pattern.compile(".*select\\s+([^\\s,]*).*", 34);
        }
        
        public ParsedDatabaseStatement parseStatement(final String statement) {
            final Matcher matcher = this.pattern.matcher(statement);
            if (!matcher.matches()) {
                return null;
            }
            if (DefaultDatabaseStatementParser.FROM_MATCHER.matcher(statement).find()) {
                return this.innerSelectStatement;
            }
            return this.statement;
        }
        
        public String getOperation() {
            return "select";
        }
    }
    
    private class InnerSelectStatementFactory implements StatementFactory
    {
        private final Pattern innerSelectPattern;
        
        private InnerSelectStatementFactory() {
            this.innerSelectPattern = Pattern.compile("^\\s*SELECT.*?\\sFROM\\s*\\(\\s*(SELECT.*)", 34);
        }
        
        public ParsedDatabaseStatement parseStatement(final String statement) {
            String sql = statement;
            String res = null;
            while (true) {
                final String res2 = this.findMatch(sql);
                if (res2 == null) {
                    break;
                }
                res = res2;
                sql = res2;
            }
            if (res != null) {
                return DefaultDatabaseStatementParser.this.selectStatementFactory.parseStatement(res);
            }
            return DefaultDatabaseStatementParser.this.selectStatementFactory.parseStatement(statement);
        }
        
        private String findMatch(final String statement) {
            final Matcher matcher = this.innerSelectPattern.matcher(statement);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            return null;
        }
        
        public String getOperation() {
            return "select";
        }
    }
    
    private class DDLStatementFactory extends DefaultStatementFactory
    {
        private final String type;
        
        public DDLStatementFactory(final String key, final Pattern pattern, final String type) {
            super(key, pattern, false);
            this.type = type;
        }
        
        ParsedDatabaseStatement createParsedDatabaseStatement(final String model) {
            return new ParsedDatabaseStatement(this.type, this.key, this.isMetricGenerator());
        }
    }
    
    private interface StatementFactory
    {
        String getOperation();
        
        ParsedDatabaseStatement parseStatement(String p0);
    }
}
