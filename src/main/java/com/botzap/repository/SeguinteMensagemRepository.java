package com.botzap.repository;

import com.botzap.domain.SeguinteMensagem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the SeguinteMensagem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SeguinteMensagemRepository extends ReactiveCrudRepository<SeguinteMensagem, Long>, SeguinteMensagemRepositoryInternal {
    @Override
    <S extends SeguinteMensagem> Mono<S> save(S entity);

    @Override
    Flux<SeguinteMensagem> findAll();

    @Override
    Mono<SeguinteMensagem> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SeguinteMensagemRepositoryInternal {
    <S extends SeguinteMensagem> Mono<S> save(S entity);

    Flux<SeguinteMensagem> findAllBy(Pageable pageable);

    Flux<SeguinteMensagem> findAll();

    Mono<SeguinteMensagem> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<SeguinteMensagem> findAllBy(Pageable pageable, Criteria criteria);
}
