package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class ComparisonCondition extends Condition {
    private final Field leftField;
    private final Operator operator;
    private final Field rightField;

    public ComparisonCondition(
        Field leftField,
        Operator operator,
        Field rightField
    ) {
        this.leftField = leftField;
        this.operator = operator;
        this.rightField = rightField;
    }

    @Override
    public String getTemplatedQuery() {
        return leftField.getTemplatedQuery() + operator + rightField.getTemplatedQuery();
    }

    @Override
    public List<Object> getParameters() {
        ArrayList<Object> parameters = new ArrayList<>();
        parameters.addAll(leftField.getParameters());
        parameters.addAll(rightField.getParameters());
        return parameters;
    }

    public enum Operator {
        EQUALS("="),
        NOT_EQUALS("!="),
        LARGER_THAN(">"),
        LARGER_OR_EQUAL(">="),
        SMALLER_THAN("<"),
        SMALLER_OR_EQUAL("<="),
        LIKE("LIKE"),
        IS("IS")
        ;

        private final String value;

        Operator(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }
}
