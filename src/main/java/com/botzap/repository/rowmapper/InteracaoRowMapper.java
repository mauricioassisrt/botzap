package com.botzap.repository.rowmapper;

import com.botzap.domain.Interacao;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Interacao}, with proper type conversions.
 */
@Service
public class InteracaoRowMapper implements BiFunction<Row, String, Interacao> {

    private final ColumnConverter converter;

    public InteracaoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Interacao} stored in the database.
     */
    @Override
    public Interacao apply(Row row, String prefix) {
        Interacao entity = new Interacao();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDataHora(converter.fromRow(row, prefix + "_data_hora", Instant.class));
        entity.setUsuarioId(converter.fromRow(row, prefix + "_usuario_id", Long.class));
        entity.setMensagemId(converter.fromRow(row, prefix + "_mensagem_id", Long.class));
        return entity;
    }
}
