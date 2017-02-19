package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface SQLResultRow<TColumnType extends SQLResultColumn>
    extends Iterable<SQLResultField<TColumnType>>, Map<String,SQLResultField<TColumnType>> {
    SQLResultField<TColumnType> getField(String field);
}
