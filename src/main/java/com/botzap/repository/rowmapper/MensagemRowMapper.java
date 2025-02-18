package com.botzap.repository.rowmapper;

import com.botzap.domain.Mensagem;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Mensagem}, with proper type conversions.
 */
@Service
public class MensagemRowMapper implements BiFunction<Row, String, Mensagem> {

    private final ColumnConverter converter;

    public MensagemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Mensagem} stored in the database.
     */
    @Override
    public Mensagem apply(Row row, String prefix) {
        Mensagem entity = new Mensagem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTexto(converter.fromRow(row, prefix + "_texto", String.class));
        entity.setOpcao(converter.fromRow(row, prefix + "_opcao", Integer.class));
        entity.setFluxoId(converter.fromRow(row, prefix + "_fluxo_id", Long.class));
        return entity;
    }
}
