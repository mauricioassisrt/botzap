package com.botzap.repository;

import com.botzap.domain.Mensagem;
import com.botzap.repository.rowmapper.MensagemRowMapper;
import com.botzap.repository.rowmapper.SeguinteMensagemRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Mensagem entity.
 */
@SuppressWarnings("unused")
class MensagemRepositoryInternalImpl extends SimpleR2dbcRepository<Mensagem, Long> implements MensagemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SeguinteMensagemRowMapper seguintemensagemMapper;
    private final MensagemRowMapper mensagemMapper;

    private static final Table entityTable = Table.aliased("mensagem", EntityManager.ENTITY_ALIAS);
    private static final Table fluxoTable = Table.aliased("seguinte_mensagem", "fluxo");

    public MensagemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SeguinteMensagemRowMapper seguintemensagemMapper,
        MensagemRowMapper mensagemMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Mensagem.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.seguintemensagemMapper = seguintemensagemMapper;
        this.mensagemMapper = mensagemMapper;
    }

    @Override
    public Flux<Mensagem> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Mensagem> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MensagemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SeguinteMensagemSqlHelper.getColumns(fluxoTable, "fluxo"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(fluxoTable)
            .on(Column.create("fluxo_id", entityTable))
            .equals(Column.create("id", fluxoTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Mensagem.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Mensagem> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Mensagem> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Mensagem process(Row row, RowMetadata metadata) {
        Mensagem entity = mensagemMapper.apply(row, "e");
        entity.setFluxo(seguintemensagemMapper.apply(row, "fluxo"));
        return entity;
    }

    @Override
    public <S extends Mensagem> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
