package com.botzap.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MensagemSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("texto", table, columnPrefix + "_texto"));
        columns.add(Column.aliased("opcao", table, columnPrefix + "_opcao"));

        columns.add(Column.aliased("fluxo_id", table, columnPrefix + "_fluxo_id"));
        return columns;
    }
}
