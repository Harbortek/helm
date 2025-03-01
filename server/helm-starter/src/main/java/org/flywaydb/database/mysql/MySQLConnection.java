package org.flywaydb.database.mysql;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.database.base.Connection;
import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.util.StringUtils;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;

@Slf4j
/**
 * MySQL implementation of Connection.
 * 正对阿里云rds的mysql数据库，因为无法提供performance_schema，所以只能去除相关的操作
 */
public class MySQLConnection extends Connection<MySQLDatabase> {
    private static final String USER_VARIABLES_TABLE_MARIADB = "information_schema.user_variables";
    private static final String USER_VARIABLES_TABLE_MYSQL = "performance_schema.user_variables_by_thread";
    private static final String FOREIGN_KEY_CHECKS = "foreign_key_checks";
    private static final String SQL_SAFE_UPDATES = "sql_safe_updates";

//    private final String userVariablesQuery;
//    private final boolean canResetUserVariables;
//
//    private final int originalForeignKeyChecks;
//    private final int originalSqlSafeUpdates;

    public MySQLConnection(MySQLDatabase database, java.sql.Connection connection) {
        super(database, connection);

//        userVariablesQuery = "SELECT variable_name FROM "
//                + (database.isMariaDB() ? USER_VARIABLES_TABLE_MARIADB : USER_VARIABLES_TABLE_MYSQL)
//                + " WHERE variable_value IS NOT NULL";
//        canResetUserVariables = hasUserVariableResetCapability();
//
//        originalForeignKeyChecks = getIntVariableValue(FOREIGN_KEY_CHECKS);
//        originalSqlSafeUpdates = getIntVariableValue(SQL_SAFE_UPDATES);
    }

//    private int getIntVariableValue(String varName) {
//        try {
//            return jdbcTemplate.queryForInt("SELECT @@" + varName);
//        } catch (SQLException e) {
//            throw new FlywaySqlException("Unable to determine value for '" + varName + "' variable", e);
//        }
//    }

    // #2215: ensure the database is recent enough and the current user has the necessary SELECT grant
//    private boolean hasUserVariableResetCapability() {
//        if (database.isMariaDB() && !database.getVersion().isAtLeast("10.2")) {
//            LOG.debug("Disabled user variable reset as it is only available from MariaDB 10.2 onwards");
//            return false;
//        }
//        if (!database.isMariaDB() && !database.getVersion().isAtLeast("5.7")) {
//            LOG.debug("Disabled user variable reset as it is only available from MySQL 5.7 onwards");
//            return false;
//        }
//
//        try {
//            jdbcTemplate.queryForStringList(userVariablesQuery);
//            return true;
//        } catch (SQLException e) {
//            LOG.debug("Disabled user variable reset as "
//                              + (database.isMariaDB() ? USER_VARIABLES_TABLE_MARIADB : USER_VARIABLES_TABLE_MYSQL)
//                              + " cannot be queried (SQL State: " + e.getSQLState() + ", Error Code: " + e.getErrorCode() + ")");
//            return false;
//        }
//    }

    @Override
    protected void doRestoreOriginalState() throws SQLException {
//        resetUserVariables();
//        jdbcTemplate.execute("SET " + FOREIGN_KEY_CHECKS + "=?, " + SQL_SAFE_UPDATES + "=?",
//                             originalForeignKeyChecks, originalSqlSafeUpdates);
    }

    // #2197: prevent user-defined variables from leaking beyond the scope of a migration
//    private void resetUserVariables() throws SQLException {
//        if (canResetUserVariables) {
//            List<String> userVariables = jdbcTemplate.queryForStringList(userVariablesQuery);
//            if (!userVariables.isEmpty()) {
//                boolean first = true;
//                StringBuilder setStatement = new StringBuilder("SET ");
//                for (String userVariable : userVariables) {
//                    if (first) {
//                        first = false;
//                    } else {
//                        setStatement.append(",");
//                    }
//                    setStatement.append("@").append(userVariable).append("=NULL");
//                }
//                jdbcTemplate.executeStatement(setStatement.toString());
//            }
//        }
//    }

    @Override
    protected String getCurrentSchemaNameOrSearchPath() throws SQLException {
        return jdbcTemplate.queryForString("SELECT DATABASE()");
    }

    @Override
    public void doChangeCurrentSchemaOrSearchPathTo(String schema) throws SQLException {
        if (StringUtils.hasLength(schema)) {
            jdbcTemplate.getConnection().setCatalog(schema);
        } else {
            try {
                // Weird hack to switch back to no database selected...
                String newDb = database.quote(UUID.randomUUID().toString());
                jdbcTemplate.execute("CREATE SCHEMA " + newDb);
                jdbcTemplate.execute("USE " + newDb);
                jdbcTemplate.execute("DROP SCHEMA " + newDb);
            } catch (Exception e) {
                log.warn("Unable to restore connection to having no default schema: " + e.getMessage());
            }
        }
    }

    @Override
    protected Schema doGetCurrentSchema() throws SQLException {
        String schemaName = getCurrentSchemaNameOrSearchPath();

        // #2206: MySQL and MariaDB can have URLs where no current schema is set, so we must handle this case explicitly.
        return schemaName == null ? null : getSchema(schemaName);
    }

    @Override
    public Schema getSchema(String name) {
        return new MySQLSchema(jdbcTemplate, database, name);
    }

    @Override
    public <T> T lock(Table table, Callable<T> callable) {
        if (canUseNamedLockTemplate()) {
            return new MySQLNamedLockTemplate(jdbcTemplate, table.toString().hashCode()).execute(callable);
        }
        return super.lock(table, callable);
    }

    protected boolean canUseNamedLockTemplate() {
        return !database.isPxcStrict() && !database.isWsrepOn();
    }
}
