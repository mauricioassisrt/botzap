package com.botzap.service;

import com.botzap.domain.Interacao;
import com.botzap.repository.InteracaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.botzap.domain.Interacao}.
 */
@Service
@Transactional
public class InteracaoService {

    private static final Logger LOG = LoggerFactory.getLogger(InteracaoService.class);

    private final InteracaoRepository interacaoRepository;

    public InteracaoService(InteracaoRepository interacaoRepository) {
        this.interacaoRepository = interacaoRepository;
    }

    /**
     * Save a interacao.
     *
     * @param interacao the entity to save.
     * @return the persisted entity.
     */
    public Mono<Interacao> save(Interacao interacao) {
        LOG.debug("Request to save Interacao : {}", interacao);
        return interacaoRepository.save(interacao);
    }

    /**
     * Update a interacao.
     *
     * @param interacao the entity to save.
     * @return the persisted entity.
     */
    public Mono<Interacao> update(Interacao interacao) {
        LOG.debug("Request to update Interacao : {}", interacao);
        return interacaoRepository.save(interacao);
    }

    /**
     * Partially update a interacao.
     *
     * @param interacao the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Interacao> partialUpdate(Interacao interacao) {
        LOG.debug("Request to partially update Interacao : {}", interacao);

        return interacaoRepository
            .findById(interacao.getId())
            .map(existingInteracao -> {
                if (interacao.getDataHora() != null) {
                    existingInteracao.setDataHora(interacao.getDataHora());
                }

                return existingInteracao;
            })
            .flatMap(interacaoRepository::save);
    }

    /**
     * Get all the interacaos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Interacao> findAll(Pageable pageable) {
        LOG.debug("Request to get all Interacaos");
        return interacaoRepository.findAllBy(pageable);
    }

    /**
     * Get all the interacaos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Interacao> findAllWithEagerRelationships(Pageable pageable) {
        return interacaoRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of interacaos available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return interacaoRepository.count();
    }

    /**
     * Get one interacao by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Interacao> findOne(Long id) {
        LOG.debug("Request to get Interacao : {}", id);
        return interacaoRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the interacao by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Interacao : {}", id);
        return interacaoRepository.deleteById(id);
    }
}
