package com.opsbears.webcomponents.sql;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.internal.XAResourceHolderState;
import bitronix.tm.recovery.RecoveryException;
import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.common.XAResourceHolder;
import bitronix.tm.resource.common.XAResourceProducer;
import bitronix.tm.resource.common.XAStatefulHolder;
import bitronix.tm.resource.ehcache.EhCacheXAResourceProducer;
import com.opsbears.webcomponents.sql.JDBCHSQLDatabaseConnection;
import com.opsbears.webcomponents.sql.JDBCMySQLDatabaseConnection;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.transaction.*;
import javax.transaction.xa.XAResource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class JDBCHSQLConnectionTest {
    private JDBCHSQLDatabaseConnection getConnection() {
        JDBCHSQLDatabaseConnection connection = new JDBCHSQLDatabaseConnection(
            "jdbc:hsqldb:mem:test",
            "test",
            ""
        );
        connection.query(
            "DROP TABLE IF EXISTS test"
        );
        connection.query(
            "CREATE TABLE IF NOT EXISTS test (\n" +
            "  id INTEGER IDENTITY PRIMARY KEY,\n" +
            "  text_field VARCHAR(255),\n" +
            "  date_field DATETIME,\n" +
            "  float_field DOUBLE,\n" +
            "  bool_field BOOLEAN\n" +
            ")\n"
        );
        return connection;
    }

    @Test
    public void testInsertQuery() {
        getConnection().query(
            "INSERT INTO test (\n" +
            "  text_field,\n" +
            "  date_field,\n" +
            "  float_field,\n" +
            "  bool_field\n" +
            ") VALUES (\n" +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?" +
            ")",
            "test",
            new Date(),
            1.2,
            false
        );
        getConnection().query(
            "INSERT INTO test (\n" +
                "  text_field,\n" +
                "  date_field,\n" +
                "  float_field,\n" +
                "  bool_field\n" +
                ") VALUES (\n" +
                "  ?," +
                "  ?," +
                "  ?," +
                "  ?" +
                ")",
            "test",
            LocalDateTime.now(),
            1.2,
            false
        );
    }

    @Test
    public void testTransaction() throws Exception {
        TransactionManager transactionManager = new BitronixTransactionManager();
        JDBCHSQLDatabaseConnection connection = getConnection();
        EhCacheXAResourceProducer.registerXAResource("hsqldb", connection.getXAResource());
        transactionManager.begin();
        Transaction transaction = transactionManager.getTransaction();
        connection.query(
                transaction,
                "INSERT INTO test (\n" +
                    "  text_field,\n" +
                    "  date_field,\n" +
                    "  float_field,\n" +
                    "  bool_field\n" +
                    ") VALUES (\n" +
                    "  ?," +
                    "  ?," +
                    "  ?," +
                    "  ?" +
                    ")",
                "test",
                new Date(),
                1.2,
                false
            );
        connection.query(
            transaction,
            "INSERT INTO test (\n" +
                "  text_field,\n" +
                "  date_field,\n" +
                "  float_field,\n" +
                "  bool_field\n" +
                ") VALUES (\n" +
                "  ?," +
                "  ?," +
                "  ?," +
                "  ?" +
                ")",
            "test",
            new Date(),
            1.3,
            false
        );
        transaction.commit();

        transactionManager.begin();
        transaction = transactionManager.getTransaction();
        connection.query(
            transaction,
            "INSERT INTO test (\n" +
                "  text_field,\n" +
                "  date_field,\n" +
                "  float_field,\n" +
                "  bool_field\n" +
                ") VALUES (\n" +
                "  ?," +
                "  ?," +
                "  ?," +
                "  ?" +
                ")",
            "test",
            new Date(),
            1.3,
            false
        );
        transaction.rollback();

        BufferedSQLResultTable result = connection.query("SELECT bool_field FROM test");
        assertEquals(false, result.getRow(1).getField("BOOL_FIELD").getValue());
        assertEquals(2, result.size());
    }
}
