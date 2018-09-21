package com.opsbears.webcomponents.sql.mapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ParametersAreNonnullByDefault
@Table("entitytest")
public class TestEntity2 {
    private Integer idField;
    private String  textField;
    private LocalDate    dateField;
    private Double  floatField;
    private Boolean boolField;

    public TestEntity2(
        @Column("id") Integer idField,
        @Column("text_field") String textField,
        @Column("date_field") LocalDate dateField,
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
    public LocalDate getDateField() {
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
