package com.opsbars.webcomponents.sql.querybuilder;

import com.opsbears.webcomponents.sql.querybuilder.SimpleTableSpec;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;

import static junit.framework.TestCase.assertEquals;

@ParametersAreNonnullByDefault
public class SimpleTableSpecTest {
    @Test
    public void test() {
        assertEquals("testtable", new SimpleTableSpec("testtable", null).toString());
        assertEquals("testtable LEFT JOIN otherTable ON testtable.a=otherTable.b", new SimpleTableSpec("testtable", null).leftJoin("otherTable", null, "a", "b").toString());
    }
}
