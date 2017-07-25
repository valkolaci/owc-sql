package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
abstract public class Field {
    abstract public String getTemplatedQuery();
    abstract public List<Object> getParameters();
}
