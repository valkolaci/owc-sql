package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DataMapperException extends RuntimeException {
    public DataMapperException() {
    }

    public DataMapperException(String s) {
        super(s);
    }

    public DataMapperException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DataMapperException(Throwable throwable) {
        super(throwable);
    }

    public DataMapperException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
