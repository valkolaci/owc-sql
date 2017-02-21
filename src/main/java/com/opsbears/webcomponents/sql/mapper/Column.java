package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ParametersAreNonnullByDefault
public @interface Column {
    String value();
}
