package com.botzap.repository;

import com.botzap.domain.Interacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Interacao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InteracaoRepository extends ReactiveCrudRepository<Interacao, Long>, InteracaoRepositoryInternal {
    Flux<Interacao> findAllBy(Pageable pageable);

    @Override
    Mono<Interacao> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Interacao> findAllWithEagerRelationships();

    @Override
    Flux<Interacao> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM interacao entity WHERE entity.usuario_id = :id")
    Flux<Interacao> findByUsuario(Long id);

    @Query("SELECT * FROM interacao entity WHERE entity.usuario_id IS NULL")
    Flux<Interacao> findAllWhereUsuarioIsNull();

    @Query("SELECT * FROM interacao entity WHERE entity.mensagem_id = :id")
    Flux<Interacao> findByMensagem(Long id);

    @Query("SELECT * FROM interacao entity WHERE entity.mensagem_id IS NULL")
    Flux<Interacao> findAllWhereMensagemIsNull();

    @Override
    <S extends Interacao> Mono<S> save(S entity);

    @Override
    Flux<Interacao> findAll();

    @Override
    Mono<Interacao> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InteracaoRepositoryInternal {
    <S extends Interacao> Mono<S> save(S entity);

    Flux<Interacao> findAllBy(Pageable pageable);

    Flux<Interacao> findAll();

    Mono<Interacao> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Interacao> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Interacao> findOneWithEagerRelationships(Long id);

    Flux<Interacao> findAllWithEagerRelationships();

    Flux<Interacao> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
