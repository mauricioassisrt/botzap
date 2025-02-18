package com.botzap.web.rest;

import com.botzap.domain.SeguinteMensagem;
import com.botzap.repository.SeguinteMensagemRepository;
import com.botzap.service.SeguinteMensagemService;
import com.botzap.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.botzap.domain.SeguinteMensagem}.
 */
@RestController
@RequestMapping("/api/seguinte-mensagems")
public class SeguinteMensagemResource {

    private static final Logger LOG = LoggerFactory.getLogger(SeguinteMensagemResource.class);

    private static final String ENTITY_NAME = "seguinteMensagem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SeguinteMensagemService seguinteMensagemService;

    private final SeguinteMensagemRepository seguinteMensagemRepository;

    public SeguinteMensagemResource(
        SeguinteMensagemService seguinteMensagemService,
        SeguinteMensagemRepository seguinteMensagemRepository
    ) {
        this.seguinteMensagemService = seguinteMensagemService;
        this.seguinteMensagemRepository = seguinteMensagemRepository;
    }

    /**
     * {@code POST  /seguinte-mensagems} : Create a new seguinteMensagem.
     *
     * @param seguinteMensagem the seguinteMensagem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new seguinteMensagem, or with status {@code 400 (Bad Request)} if the seguinteMensagem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SeguinteMensagem>> createSeguinteMensagem(@Valid @RequestBody SeguinteMensagem seguinteMensagem)
        throws URISyntaxException {
        LOG.debug("REST request to save SeguinteMensagem : {}", seguinteMensagem);
        if (seguinteMensagem.getId() != null) {
            throw new BadRequestAlertException("A new seguinteMensagem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return seguinteMensagemService
            .save(seguinteMensagem)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/seguinte-mensagems/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /seguinte-mensagems/:id} : Updates an existing seguinteMensagem.
     *
     * @param id the id of the seguinteMensagem to save.
     * @param seguinteMensagem the seguinteMensagem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seguinteMensagem,
     * or with status {@code 400 (Bad Request)} if the seguinteMensagem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the seguinteMensagem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SeguinteMensagem>> updateSeguinteMensagem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SeguinteMensagem seguinteMensagem
    ) throws URISyntaxException {
        LOG.debug("REST request to update SeguinteMensagem : {}, {}", id, seguinteMensagem);
        if (seguinteMensagem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seguinteMensagem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return seguinteMensagemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return seguinteMensagemService
                    .update(seguinteMensagem)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /seguinte-mensagems/:id} : Partial updates given fields of an existing seguinteMensagem, field will ignore if it is null
     *
     * @param id the id of the seguinteMensagem to save.
     * @param seguinteMensagem the seguinteMensagem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated seguinteMensagem,
     * or with status {@code 400 (Bad Request)} if the seguinteMensagem is not valid,
     * or with status {@code 404 (Not Found)} if the seguinteMensagem is not found,
     * or with status {@code 500 (Internal Server Error)} if the seguinteMensagem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SeguinteMensagem>> partialUpdateSeguinteMensagem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SeguinteMensagem seguinteMensagem
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SeguinteMensagem partially : {}, {}", id, seguinteMensagem);
        if (seguinteMensagem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, seguinteMensagem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return seguinteMensagemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SeguinteMensagem> result = seguinteMensagemService.partialUpdate(seguinteMensagem);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /seguinte-mensagems} : get all the seguinteMensagems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of seguinteMensagems in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<SeguinteMensagem>> getAllSeguinteMensagems() {
        LOG.debug("REST request to get all SeguinteMensagems");
        return seguinteMensagemService.findAll().collectList();
    }

    /**
     * {@code GET  /seguinte-mensagems} : get all the seguinteMensagems as a stream.
     * @return the {@link Flux} of seguinteMensagems.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SeguinteMensagem> getAllSeguinteMensagemsAsStream() {
        LOG.debug("REST request to get all SeguinteMensagems as a stream");
        return seguinteMensagemService.findAll();
    }

    /**
     * {@code GET  /seguinte-mensagems/:id} : get the "id" seguinteMensagem.
     *
     * @param id the id of the seguinteMensagem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the seguinteMensagem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SeguinteMensagem>> getSeguinteMensagem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SeguinteMensagem : {}", id);
        Mono<SeguinteMensagem> seguinteMensagem = seguinteMensagemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(seguinteMensagem);
    }

    /**
     * {@code DELETE  /seguinte-mensagems/:id} : delete the "id" seguinteMensagem.
     *
     * @param id the id of the seguinteMensagem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSeguinteMensagem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SeguinteMensagem : {}", id);
        return seguinteMensagemService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
