package com.opsbears.webcomponents.sql.querybuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SimpleTableSpec implements TableSpec {
    private final String tableName;
    @Nullable
    private final String alias;
    private SimpleTableSpec parent = null;
    private String joinType = null;
    private String joinColumn = null;
    private String joinTargetColumn = null;
    private SimpleTableSpec joinTarget = null;

    public SimpleTableSpec(
        String tableName
    ) {
        this.tableName = tableName;
        this.alias = null;
    }

    public SimpleTableSpec(
        String tableName,
        @Nullable String alias
    ) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public SimpleTableSpec leftJoin(String tableName, @Nullable String alias, String sourceColumn, String targetColumn) {
        joinTarget = new SimpleTableSpec(tableName, alias);
        joinTarget.parent = this;
        joinType = "LEFT";
        joinColumn = sourceColumn;
        joinTargetColumn = targetColumn;
        return joinTarget;
    }

    public SimpleTableSpec innerJoin(String tableName, @Nullable String alias, String sourceColumn, String targetColumn) {
        joinTarget = new SimpleTableSpec(tableName, alias);
        joinTarget.parent = this;
        joinType = "INNER";
        joinColumn = sourceColumn;
        joinTargetColumn = targetColumn;
        return joinTarget;

    }

    public SimpleTableSpec rightJoin(String tableName, @Nullable String alias, String sourceColumn, String targetColumn) {
        joinTarget = new SimpleTableSpec(tableName, alias);
        joinTarget.parent = this;
        joinType = "RIGHT";
        joinColumn = sourceColumn;
        joinTargetColumn = targetColumn;
        return joinTarget;
    }

    @Override
    @Nullable
    public String getRootTableName() {
        if (parent != null) {
            return parent.getRootTableName();
        }
        return alias == null?tableName:alias;
    }

    public String toString() {
        if (parent != null) {
            return parent.toString();
        }
        StringBuilder sql = new StringBuilder(tableName + (alias == null ? "" : " " + alias));
        SimpleTableSpec currentTableList = this;
        SimpleTableSpec lastTableList = null;
        while (currentTableList.joinTarget != null) {
            lastTableList = currentTableList;
            currentTableList = currentTableList.joinTarget;
            sql.append(" ").append(lastTableList.joinType).append(" JOIN ").append(currentTableList.tableName).append(currentTableList.alias == null ? "" : " " + currentTableList.alias).append(" ON ");
            sql.append(lastTableList.alias == null ? lastTableList.tableName : lastTableList.alias).append(".").append(lastTableList.joinColumn).append("=").append(currentTableList.alias == null ? currentTableList.tableName : currentTableList.alias).append(".").append(lastTableList.joinTargetColumn);
        }
        return sql.toString();
    }
}
