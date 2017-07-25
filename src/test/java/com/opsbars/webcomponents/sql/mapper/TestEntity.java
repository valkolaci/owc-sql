package com.opsbears.webcomponents.sql.mapper;

import com.opsbears.webcomponents.sql.mapper.Column;
import com.opsbears.webcomponents.sql.mapper.Primary;
import com.opsbears.webcomponents.sql.mapper.Table;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.util.Date;

@ParametersAreNonnullByDefault
@Table("entitytest")
public class TestEntity {
    private Integer idField;
    private String  textField;
    private LocalDateTime    dateField;
    private Double  floatField;
    private Boolean boolField;

    public TestEntity(
        @Column("id") Integer idField,
        @Column("text_field") String textField,
        @Column("date_field") LocalDateTime dateField,
        @Column("float_field") Double floatField,
        @Column("bool_field") Boolean boolField
    ) {
        this.idField = idField;
        this.textField = textField;
        this.dateField = dateField;
        this.floatField = floatField;
        this.boolField = boolField;
    }

    @Primary
    @Column("id")
    public Integer getIdField() {
        return idField;
    }

    @Column("text_field")
    public String getTextField() {
        return textField;
    }

    @Column("date_field")
    public LocalDateTime getDateField() {
        return dateField;
    }

    @Column("float_field")
    public Double getFloatField() {
        return floatField;
    }

    @Column("bool_field")
    public Boolean getBoolField() {
        return boolField;
    }
}
