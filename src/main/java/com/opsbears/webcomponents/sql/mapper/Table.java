package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ParametersAreNonnullByDefault
public @interface Table {
    String value();
}
