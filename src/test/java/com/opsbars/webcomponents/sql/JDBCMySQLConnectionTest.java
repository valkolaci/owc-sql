package com.opsbears.webcomponents.sql;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.resource.ehcache.EhCacheXAResourceProducer;
import com.opsbears.webcomponents.sql.JDBCMySQLDatabaseConnection;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class JDBCMySQLConnectionTest {
    private JDBCMySQLDatabaseConnection getConnection() {
        JDBCMySQLDatabaseConnection connection = new JDBCMySQLDatabaseConnection(
            "jdbc:mysql://localhost/test?characterEncoding=utf8&useUnicode=yes",
            "test",
            ""
        );
        connection.query(
            "DROP TABLE IF EXISTS test"
        );
        connection.query(
            "CREATE TABLE IF NOT EXISTS test (\n" +
            "  id BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
            "  text_field VARCHAR(255),\n" +
            "  date_field DATETIME,\n" +
            "  float_field DOUBLE(8,2),\n" +
            "  bool_field BOOL\n" +
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
        JDBCMySQLDatabaseConnection connection = getConnection();
        EhCacheXAResourceProducer.registerXAResource("mysql", connection.getXAResource());
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
            LocalDateTime.now(),
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
            LocalDateTime.now(),
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
            LocalDateTime.now(),
            1.3,
            false
        );
        transaction.rollback();

        BufferedSQLResultTable result = connection.query("SELECT bool_field FROM test");
        assertEquals(false, result.getRow(1).getField("bool_field").getValue());
        assertEquals(2, result.size());
    }
}
