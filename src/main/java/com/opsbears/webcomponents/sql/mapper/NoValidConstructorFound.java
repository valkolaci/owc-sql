package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NoValidConstructorFound extends DataMapperException {
    public NoValidConstructorFound(String s) {
        super(s);
    }
}
