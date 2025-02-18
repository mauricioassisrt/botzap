package com.botzap.repository;

import com.botzap.domain.Mensagem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Mensagem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MensagemRepository extends ReactiveCrudRepository<Mensagem, Long>, MensagemRepositoryInternal {
    Flux<Mensagem> findAllBy(Pageable pageable);

    @Override
    Mono<Mensagem> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Mensagem> findAllWithEagerRelationships();

    @Override
    Flux<Mensagem> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM mensagem entity WHERE entity.fluxo_id = :id")
    Flux<Mensagem> findByFluxo(Long id);

    @Query("SELECT * FROM mensagem entity WHERE entity.fluxo_id IS NULL")
    Flux<Mensagem> findAllWhereFluxoIsNull();

    @Override
    <S extends Mensagem> Mono<S> save(S entity);

    @Override
    Flux<Mensagem> findAll();

    @Override
    Mono<Mensagem> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MensagemRepositoryInternal {
    <S extends Mensagem> Mono<S> save(S entity);

    Flux<Mensagem> findAllBy(Pageable pageable);

    Flux<Mensagem> findAll();

    Mono<Mensagem> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Mensagem> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Mensagem> findOneWithEagerRelationships(Long id);

    Flux<Mensagem> findAllWithEagerRelationships();

    Flux<Mensagem> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
