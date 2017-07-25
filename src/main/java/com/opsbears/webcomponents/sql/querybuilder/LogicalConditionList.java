package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class LogicalConditionList extends Condition {
    private final Type type;
    private final ConditionList conditions;

    public LogicalConditionList(
        Type type,
        ConditionList conditions
    ) {
        this.type = type;
        this.conditions = conditions;
    }

    public Type getType() {
        return type;
    }

    public ConditionList getConditions() {
        return conditions;
    }

    @Override
    public String getTemplatedQuery() {
        return
            "(" +
                conditions
                    .stream()
                    .map(Condition::getTemplatedQuery)
                    .collect(Collectors.joining(" " + type.toString() + " ")) +
            ")";
    }

    @Override
    public List<Object> getParameters() {
        List<Object> parameterList = new ArrayList<>();
        for (Condition condition : conditions) {
            parameterList.addAll(condition.getParameters());
        }
        return parameterList;
    }

    public enum Type {
        AND,
        OR;
    }
}
