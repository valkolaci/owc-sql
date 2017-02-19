package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class UnbufferedResultColumn extends ResultColumn implements UnbufferedSQLResultColumn {
    UnbufferedResultColumn(String name) {
        super(name);
    }
}
