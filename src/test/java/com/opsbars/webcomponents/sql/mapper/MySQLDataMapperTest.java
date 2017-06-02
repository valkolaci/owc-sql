package com.opsbars.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.JDBCMySQLConnectionConfiguration;
import com.opsbears.webcomponents.sql.JDBCMySQLConnectionFactory;
import com.opsbears.webcomponents.sql.MySQLDatabaseConnection;
import com.opsbears.webcomponents.sql.mapper.DataMapper;
import com.opsbears.webcomponents.sql.mapper.MySQLDataMapper;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

@ParametersAreNonnullByDefault
public class MySQLDataMapperTest {
    private DataMapper getMapper() {
        Map<String, JDBCMySQLConnectionConfiguration> configMap = new HashMap<>();
        configMap.put("default", new JDBCMySQLConnectionConfiguration(
            "jdbc:mysql://localhost/test?characterEncoding=utf8&useUnicode=yes",
            "test",
            ""
        ));
        JDBCMySQLConnectionFactory factory = new JDBCMySQLConnectionFactory(
            configMap
        );
        MySQLDatabaseConnection connection = factory.getConnection();
        connection.query(
            "DROP TABLE IF EXISTS entitytest"
        );
        connection.query(
            "CREATE TABLE IF NOT EXISTS entitytest (\n" +
            "  id BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
            "  text_field VARCHAR(255),\n" +
            "  date_field DATETIME,\n" +
            "  float_field DOUBLE(8,2),\n" +
            "  bool_field BOOL\n" +
            ")\n"
        );

        return new MySQLDataMapper(factory);
    }

    @Test
    public void testSimpleStoreLoad() {
        DataMapper mapper = getMapper();
        LocalDateTime date = Instant.ofEpochMilli(1489943447000L).atZone(ZoneId.systemDefault()).toLocalDateTime();
        TestEntity entity = new TestEntity(
            1,
            "Test",
            date,
            1.2,
            true
        );
        mapper.store(entity);
        TestEntity loadedEntity = mapper.loadOneBy(TestEntity.class, "id", 1);

        assertEquals(1, loadedEntity.getIdField().intValue());
        assertEquals("Test", loadedEntity.getTextField());
        assertEquals(1489943447000L, loadedEntity.getDateField().toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())).getEpochSecond()*1000);
        assertEquals(1.2, loadedEntity.getFloatField());
        assertEquals(true, loadedEntity.getBoolField().booleanValue());
    }
}
