package com.botzap.repository;

import com.botzap.domain.Interacao;
import com.botzap.repository.rowmapper.InteracaoRowMapper;
import com.botzap.repository.rowmapper.MensagemRowMapper;
import com.botzap.repository.rowmapper.UsuarioRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Interacao entity.
 */
@SuppressWarnings("unused")
class InteracaoRepositoryInternalImpl extends SimpleR2dbcRepository<Interacao, Long> implements InteracaoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UsuarioRowMapper usuarioMapper;
    private final MensagemRowMapper mensagemMapper;
    private final InteracaoRowMapper interacaoMapper;

    private static final Table entityTable = Table.aliased("interacao", EntityManager.ENTITY_ALIAS);
    private static final Table usuarioTable = Table.aliased("usuario", "usuario");
    private static final Table mensagemTable = Table.aliased("mensagem", "mensagem");

    public InteracaoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UsuarioRowMapper usuarioMapper,
        MensagemRowMapper mensagemMapper,
        InteracaoRowMapper interacaoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Interacao.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.usuarioMapper = usuarioMapper;
        this.mensagemMapper = mensagemMapper;
        this.interacaoMapper = interacaoMapper;
    }

    @Override
    public Flux<Interacao> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Interacao> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = InteracaoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UsuarioSqlHelper.getColumns(usuarioTable, "usuario"));
        columns.addAll(MensagemSqlHelper.getColumns(mensagemTable, "mensagem"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(usuarioTable)
            .on(Column.create("usuario_id", entityTable))
            .equals(Column.create("id", usuarioTable))
            .leftOuterJoin(mensagemTable)
            .on(Column.create("mensagem_id", entityTable))
            .equals(Column.create("id", mensagemTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Interacao.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Interacao> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Interacao> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Interacao process(Row row, RowMetadata metadata) {
        Interacao entity = interacaoMapper.apply(row, "e");
        entity.setUsuario(usuarioMapper.apply(row, "usuario"));
        entity.setMensagem(mensagemMapper.apply(row, "mensagem"));
        return entity;
    }

    @Override
    public <S extends Interacao> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
