package com.botzap.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class InteracaoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("data_hora", table, columnPrefix + "_data_hora"));

        columns.add(Column.aliased("usuario_id", table, columnPrefix + "_usuario_id"));
        columns.add(Column.aliased("mensagem_id", table, columnPrefix + "_mensagem_id"));
        return columns;
    }
}
