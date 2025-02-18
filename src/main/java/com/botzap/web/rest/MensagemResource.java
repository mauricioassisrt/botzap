package com.botzap.web.rest;

import com.botzap.domain.Mensagem;
import com.botzap.repository.MensagemRepository;
import com.botzap.service.MensagemService;
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
 * REST controller for managing {@link com.botzap.domain.Mensagem}.
 */
@RestController
@RequestMapping("/api/mensagems")
public class MensagemResource {

    private static final Logger LOG = LoggerFactory.getLogger(MensagemResource.class);

    private static final String ENTITY_NAME = "mensagem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MensagemService mensagemService;

    private final MensagemRepository mensagemRepository;

    public MensagemResource(MensagemService mensagemService, MensagemRepository mensagemRepository) {
        this.mensagemService = mensagemService;
        this.mensagemRepository = mensagemRepository;
    }

    /**
     * {@code POST  /mensagems} : Create a new mensagem.
     *
     * @param mensagem the mensagem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mensagem, or with status {@code 400 (Bad Request)} if the mensagem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Mensagem>> createMensagem(@Valid @RequestBody Mensagem mensagem) throws URISyntaxException {
        LOG.debug("REST request to save Mensagem : {}", mensagem);
        if (mensagem.getId() != null) {
            throw new BadRequestAlertException("A new mensagem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return mensagemService
            .save(mensagem)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/mensagems/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /mensagems/:id} : Updates an existing mensagem.
     *
     * @param id the id of the mensagem to save.
     * @param mensagem the mensagem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mensagem,
     * or with status {@code 400 (Bad Request)} if the mensagem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mensagem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Mensagem>> updateMensagem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Mensagem mensagem
    ) throws URISyntaxException {
        LOG.debug("REST request to update Mensagem : {}, {}", id, mensagem);
        if (mensagem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mensagem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mensagemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return mensagemService
                    .update(mensagem)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /mensagems/:id} : Partial updates given fields of an existing mensagem, field will ignore if it is null
     *
     * @param id the id of the mensagem to save.
     * @param mensagem the mensagem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mensagem,
     * or with status {@code 400 (Bad Request)} if the mensagem is not valid,
     * or with status {@code 404 (Not Found)} if the mensagem is not found,
     * or with status {@code 500 (Internal Server Error)} if the mensagem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Mensagem>> partialUpdateMensagem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Mensagem mensagem
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Mensagem partially : {}, {}", id, mensagem);
        if (mensagem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mensagem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return mensagemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Mensagem> result = mensagemService.partialUpdate(mensagem);

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
     * {@code GET  /mensagems} : get all the mensagems.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mensagems in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Mensagem>>> getAllMensagems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Mensagems");
        return mensagemService
            .countAll()
            .zipWith(mensagemService.findAll(pageable).collectList())
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
     * {@code GET  /mensagems/:id} : get the "id" mensagem.
     *
     * @param id the id of the mensagem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mensagem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Mensagem>> getMensagem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Mensagem : {}", id);
        Mono<Mensagem> mensagem = mensagemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mensagem);
    }

    /**
     * {@code DELETE  /mensagems/:id} : delete the "id" mensagem.
     *
     * @param id the id of the mensagem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMensagem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Mensagem : {}", id);
        return mensagemService
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
