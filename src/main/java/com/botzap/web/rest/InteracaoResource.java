package com.botzap.web.rest;

import com.botzap.domain.Interacao;
import com.botzap.repository.InteracaoRepository;
import com.botzap.service.InteracaoService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.botzap.domain.Interacao}.
 */
@RestController
@RequestMapping("/api/interacaos")
public class InteracaoResource {

    private static final Logger LOG = LoggerFactory.getLogger(InteracaoResource.class);

    private static final String ENTITY_NAME = "interacao";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InteracaoService interacaoService;

    private final InteracaoRepository interacaoRepository;

    public InteracaoResource(InteracaoService interacaoService, InteracaoRepository interacaoRepository) {
        this.interacaoService = interacaoService;
        this.interacaoRepository = interacaoRepository;
    }

    /**
     * {@code POST  /interacaos} : Create a new interacao.
     *
     * @param interacao the interacao to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new interacao, or with status {@code 400 (Bad Request)} if the interacao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Interacao>> createInteracao(@Valid @RequestBody Interacao interacao) throws URISyntaxException {
        LOG.debug("REST request to save Interacao : {}", interacao);
        if (interacao.getId() != null) {
            throw new BadRequestAlertException("A new interacao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return interacaoService
            .save(interacao)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/interacaos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /interacaos/:id} : Updates an existing interacao.
     *
     * @param id the id of the interacao to save.
     * @param interacao the interacao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interacao,
     * or with status {@code 400 (Bad Request)} if the interacao is not valid,
     * or with status {@code 500 (Internal Server Error)} if the interacao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Interacao>> updateInteracao(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Interacao interacao
    ) throws URISyntaxException {
        LOG.debug("REST request to update Interacao : {}, {}", id, interacao);
        if (interacao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interacao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interacaoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return interacaoService
                    .update(interacao)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /interacaos/:id} : Partial updates given fields of an existing interacao, field will ignore if it is null
     *
     * @param id the id of the interacao to save.
     * @param interacao the interacao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interacao,
     * or with status {@code 400 (Bad Request)} if the interacao is not valid,
     * or with status {@code 404 (Not Found)} if the interacao is not found,
     * or with status {@code 500 (Internal Server Error)} if the interacao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Interacao>> partialUpdateInteracao(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Interacao interacao
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Interacao partially : {}, {}", id, interacao);
        if (interacao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interacao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interacaoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Interacao> result = interacaoService.partialUpdate(interacao);

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
     * {@code GET  /interacaos} : get all the interacaos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of interacaos in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Interacao>>> getAllInteracaos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Interacaos");
        return interacaoService
            .countAll()
            .zipWith(interacaoService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /interacaos/:id} : get the "id" interacao.
     *
     * @param id the id of the interacao to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the interacao, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Interacao>> getInteracao(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Interacao : {}", id);
        Mono<Interacao> interacao = interacaoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(interacao);
    }

    /**
     * {@code DELETE  /interacaos/:id} : delete the "id" interacao.
     *
     * @param id the id of the interacao to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInteracao(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Interacao : {}", id);
        return interacaoService
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
