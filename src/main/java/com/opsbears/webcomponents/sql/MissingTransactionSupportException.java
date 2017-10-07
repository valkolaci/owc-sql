package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MissingTransactionSupportException extends SQLException {
    public MissingTransactionSupportException() {
        super("Missing XA transaction support, cannot use transactions.");
    }

    public MissingTransactionSupportException(String s) {
        super(s);
    }

    public MissingTransactionSupportException(
        String s,
        Throwable throwable
    ) {
        super(s, throwable);
    }

    public MissingTransactionSupportException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String getQuery() {
        return "";
    }
}
