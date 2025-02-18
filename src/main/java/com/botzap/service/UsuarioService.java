package com.botzap.service;

import com.botzap.domain.Usuario;
import com.botzap.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.botzap.domain.Usuario}.
 */
@Service
@Transactional
public class UsuarioService {

    private static final Logger LOG = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Save a usuario.
     *
     * @param usuario the entity to save.
     * @return the persisted entity.
     */
    public Mono<Usuario> save(Usuario usuario) {
        LOG.debug("Request to save Usuario : {}", usuario);
        return usuarioRepository.save(usuario);
    }

    /**
     * Update a usuario.
     *
     * @param usuario the entity to save.
     * @return the persisted entity.
     */
    public Mono<Usuario> update(Usuario usuario) {
        LOG.debug("Request to update Usuario : {}", usuario);
        return usuarioRepository.save(usuario);
    }

    /**
     * Partially update a usuario.
     *
     * @param usuario the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Usuario> partialUpdate(Usuario usuario) {
        LOG.debug("Request to partially update Usuario : {}", usuario);

        return usuarioRepository
            .findById(usuario.getId())
            .map(existingUsuario -> {
                if (usuario.getNome() != null) {
                    existingUsuario.setNome(usuario.getNome());
                }
                if (usuario.getTelefone() != null) {
                    existingUsuario.setTelefone(usuario.getTelefone());
                }

                return existingUsuario;
            })
            .flatMap(usuarioRepository::save);
    }

    /**
     * Get all the usuarios.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Usuario> findAll() {
        LOG.debug("Request to get all Usuarios");
        return usuarioRepository.findAll();
    }

    /**
     * Returns the number of usuarios available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return usuarioRepository.count();
    }

    /**
     * Get one usuario by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Usuario> findOne(Long id) {
        LOG.debug("Request to get Usuario : {}", id);
        return usuarioRepository.findById(id);
    }

    /**
     * Delete the usuario by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Usuario : {}", id);
        return usuarioRepository.deleteById(id);
    }
}
