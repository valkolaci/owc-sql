package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EntityAlreadyExistsException extends DataMapperException {
    public EntityAlreadyExistsException(Throwable e) {
        super(e);
    }
}
