package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract public class SQLException extends RuntimeException {
    public SQLException(String s) {
        super(s);
    }

    public SQLException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SQLException(Throwable throwable) {
        super(throwable);
    }

    abstract public String getQuery();
}
