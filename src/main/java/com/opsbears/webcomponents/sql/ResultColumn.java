package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract class ResultColumn implements SQLResultColumn {
    private String name;

    ResultColumn(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
