package com.botzap.repository.rowmapper;

import com.botzap.domain.SeguinteMensagem;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link SeguinteMensagem}, with proper type conversions.
 */
@Service
public class SeguinteMensagemRowMapper implements BiFunction<Row, String, SeguinteMensagem> {

    private final ColumnConverter converter;

    public SeguinteMensagemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link SeguinteMensagem} stored in the database.
     */
    @Override
    public SeguinteMensagem apply(Row row, String prefix) {
        SeguinteMensagem entity = new SeguinteMensagem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        return entity;
    }
}
