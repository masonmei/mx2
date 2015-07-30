// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.db;

import com.newrelic.agent.deps.ch.qos.logback.classic.db.names.ColumnName;
import com.newrelic.agent.deps.ch.qos.logback.classic.db.names.TableName;
import com.newrelic.agent.deps.ch.qos.logback.classic.db.names.DBNameResolver;

public class SQLBuilder
{
    static String buildInsertPropertiesSQL(final DBNameResolver dbNameResolver) {
        final StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(dbNameResolver.getTableName(TableName.LOGGING_EVENT_PROPERTY)).append(" (");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.EVENT_ID)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.MAPPED_KEY)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.MAPPED_VALUE)).append(") ");
        sqlBuilder.append("VALUES (?, ?, ?)");
        return sqlBuilder.toString();
    }
    
    static String buildInsertExceptionSQL(final DBNameResolver dbNameResolver) {
        final StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(dbNameResolver.getTableName(TableName.LOGGING_EVENT_EXCEPTION)).append(" (");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.EVENT_ID)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.I)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.TRACE_LINE)).append(") ");
        sqlBuilder.append("VALUES (?, ?, ?)");
        return sqlBuilder.toString();
    }
    
    static String buildInsertSQL(final DBNameResolver dbNameResolver) {
        final StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(dbNameResolver.getTableName(TableName.LOGGING_EVENT)).append(" (");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.TIMESTMP)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.FORMATTED_MESSAGE)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.LOGGER_NAME)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.LEVEL_STRING)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.THREAD_NAME)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.REFERENCE_FLAG)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG0)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG1)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG2)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.ARG3)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_FILENAME)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_CLASS)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_METHOD)).append(", ");
        sqlBuilder.append(dbNameResolver.getColumnName(ColumnName.CALLER_LINE)).append(") ");
        sqlBuilder.append("VALUES (?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return sqlBuilder.toString();
    }
}
