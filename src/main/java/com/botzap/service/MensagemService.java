package com.botzap.service;

import com.botzap.domain.Mensagem;
import com.botzap.repository.MensagemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.botzap.domain.Mensagem}.
 */
@Service
@Transactional
public class MensagemService {

    private static final Logger LOG = LoggerFactory.getLogger(MensagemService.class);

    private final MensagemRepository mensagemRepository;

    public MensagemService(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    /**
     * Save a mensagem.
     *
     * @param mensagem the entity to save.
     * @return the persisted entity.
     */
    public Mono<Mensagem> save(Mensagem mensagem) {
        LOG.debug("Request to save Mensagem : {}", mensagem);
        return mensagemRepository.save(mensagem);
    }

    /**
     * Update a mensagem.
     *
     * @param mensagem the entity to save.
     * @return the persisted entity.
     */
    public Mono<Mensagem> update(Mensagem mensagem) {
        LOG.debug("Request to update Mensagem : {}", mensagem);
        return mensagemRepository.save(mensagem);
    }

    /**
     * Partially update a mensagem.
     *
     * @param mensagem the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Mensagem> partialUpdate(Mensagem mensagem) {
        LOG.debug("Request to partially update Mensagem : {}", mensagem);

        return mensagemRepository
            .findById(mensagem.getId())
            .map(existingMensagem -> {
                if (mensagem.getTexto() != null) {
                    existingMensagem.setTexto(mensagem.getTexto());
                }
                if (mensagem.getOpcao() != null) {
                    existingMensagem.setOpcao(mensagem.getOpcao());
                }

                return existingMensagem;
            })
            .flatMap(mensagemRepository::save);
    }

    /**
     * Get all the mensagems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Mensagem> findAll(Pageable pageable) {
        LOG.debug("Request to get all Mensagems");
        return mensagemRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of mensagems available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return mensagemRepository.count();
    }

    /**
     * Get one mensagem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Mensagem> findOne(Long id) {
        LOG.debug("Request to get Mensagem : {}", id);
        return mensagemRepository.findById(id);
    }

    /**
     * Delete the mensagem by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Mensagem : {}", id);
        return mensagemRepository.deleteById(id);
    }
}
