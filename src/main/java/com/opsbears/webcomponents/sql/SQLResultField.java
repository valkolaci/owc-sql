package com.opsbears.webcomponents.sql;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SQLResultField<TColumnType extends SQLResultColumn> {
    /**
     * Returns the name of this field
     */
    String getName();

    /**
     * Return the value as the type it is detected to be.
     */
    Object getValue();

    /**
     * Return the value as a string.
     */
    String toString();

    /**
     * Return the column this field is a part of.
     */
    TColumnType getColumn();

    /**
     * Return the row this field is a part of.
     */
    SQLResultRow<TColumnType> getRow();
}
