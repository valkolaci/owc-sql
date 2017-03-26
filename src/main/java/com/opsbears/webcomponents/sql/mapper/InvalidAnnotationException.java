package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InvalidAnnotationException extends RuntimeException {
    public InvalidAnnotationException(String s) {
        super(s);
    }
}
