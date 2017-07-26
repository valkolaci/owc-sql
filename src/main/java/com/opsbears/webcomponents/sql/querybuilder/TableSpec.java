package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface TableSpec {
    @Nullable
    String getRootTableName();
    String toString();
}
