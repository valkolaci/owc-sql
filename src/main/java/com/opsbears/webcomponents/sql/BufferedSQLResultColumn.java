package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface BufferedSQLResultColumn<TResultField extends BufferedSQLResultField> extends
     SQLResultColumn,
     Iterable<TResultField>,
     List<TResultField> {
}
