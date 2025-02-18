package com.botzap.service;

import com.botzap.domain.SeguinteMensagem;
import com.botzap.repository.SeguinteMensagemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.botzap.domain.SeguinteMensagem}.
 */
@Service
@Transactional
public class SeguinteMensagemService {

    private static final Logger LOG = LoggerFactory.getLogger(SeguinteMensagemService.class);

    private final SeguinteMensagemRepository seguinteMensagemRepository;

    public SeguinteMensagemService(SeguinteMensagemRepository seguinteMensagemRepository) {
        this.seguinteMensagemRepository = seguinteMensagemRepository;
    }

    /**
     * Save a seguinteMensagem.
     *
     * @param seguinteMensagem the entity to save.
     * @return the persisted entity.
     */
    public Mono<SeguinteMensagem> save(SeguinteMensagem seguinteMensagem) {
        LOG.debug("Request to save SeguinteMensagem : {}", seguinteMensagem);
        return seguinteMensagemRepository.save(seguinteMensagem);
    }

    /**
     * Update a seguinteMensagem.
     *
     * @param seguinteMensagem the entity to save.
     * @return the persisted entity.
     */
    public Mono<SeguinteMensagem> update(SeguinteMensagem seguinteMensagem) {
        LOG.debug("Request to update SeguinteMensagem : {}", seguinteMensagem);
        return seguinteMensagemRepository.save(seguinteMensagem);
    }

    /**
     * Partially update a seguinteMensagem.
     *
     * @param seguinteMensagem the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<SeguinteMensagem> partialUpdate(SeguinteMensagem seguinteMensagem) {
        LOG.debug("Request to partially update SeguinteMensagem : {}", seguinteMensagem);

        return seguinteMensagemRepository
            .findById(seguinteMensagem.getId())
            .map(existingSeguinteMensagem -> {
                if (seguinteMensagem.getDescricao() != null) {
                    existingSeguinteMensagem.setDescricao(seguinteMensagem.getDescricao());
                }

                return existingSeguinteMensagem;
            })
            .flatMap(seguinteMensagemRepository::save);
    }

    /**
     * Get all the seguinteMensagems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SeguinteMensagem> findAll() {
        LOG.debug("Request to get all SeguinteMensagems");
        return seguinteMensagemRepository.findAll();
    }

    /**
     * Returns the number of seguinteMensagems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return seguinteMensagemRepository.count();
    }

    /**
     * Get one seguinteMensagem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<SeguinteMensagem> findOne(Long id) {
        LOG.debug("Request to get SeguinteMensagem : {}", id);
        return seguinteMensagemRepository.findById(id);
    }

    /**
     * Delete the seguinteMensagem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete SeguinteMensagem : {}", id);
        return seguinteMensagemRepository.deleteById(id);
    }
}
