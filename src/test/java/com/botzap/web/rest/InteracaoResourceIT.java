package com.botzap.web.rest;

import static com.botzap.domain.InteracaoAsserts.*;
import static com.botzap.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.botzap.IntegrationTest;
import com.botzap.domain.Interacao;
import com.botzap.repository.EntityManager;
import com.botzap.repository.InteracaoRepository;
import com.botzap.service.InteracaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link InteracaoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InteracaoResourceIT {

    private static final Instant DEFAULT_DATA_HORA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_HORA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/interacaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InteracaoRepository interacaoRepository;

    @Mock
    private InteracaoRepository interacaoRepositoryMock;

    @Mock
    private InteracaoService interacaoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Interacao interacao;

    private Interacao insertedInteracao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interacao createEntity() {
        return new Interacao().dataHora(DEFAULT_DATA_HORA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interacao createUpdatedEntity() {
        return new Interacao().dataHora(UPDATED_DATA_HORA);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Interacao.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        interacao = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInteracao != null) {
            interacaoRepository.delete(insertedInteracao).block();
            insertedInteracao = null;
        }
        deleteEntities(em);
    }

    @Test
    void createInteracao() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Interacao
        var returnedInteracao = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Interacao.class)
            .returnResult()
            .getResponseBody();

        // Validate the Interacao in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInteracaoUpdatableFieldsEquals(returnedInteracao, getPersistedInteracao(returnedInteracao));

        insertedInteracao = returnedInteracao;
    }

    @Test
    void createInteracaoWithExistingId() throws Exception {
        // Create the Interacao with an existing ID
        interacao.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDataHoraIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        interacao.setDataHora(null);

        // Create the Interacao, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInteracaos() {
        // Initialize the database
        insertedInteracao = interacaoRepository.save(interacao).block();

        // Get all the interacaoList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(interacao.getId().intValue()))
            .jsonPath("$.[*].dataHora")
            .value(hasItem(DEFAULT_DATA_HORA.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInteracaosWithEagerRelationshipsIsEnabled() {
        when(interacaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(interacaoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInteracaosWithEagerRelationshipsIsNotEnabled() {
        when(interacaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(interacaoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInteracao() {
        // Initialize the database
        insertedInteracao = interacaoRepository.save(interacao).block();

        // Get the interacao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, interacao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(interacao.getId().intValue()))
            .jsonPath("$.dataHora")
            .value(is(DEFAULT_DATA_HORA.toString()));
    }

    @Test
    void getNonExistingInteracao() {
        // Get the interacao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInteracao() throws Exception {
        // Initialize the database
        insertedInteracao = interacaoRepository.save(interacao).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the interacao
        Interacao updatedInteracao = interacaoRepository.findById(interacao.getId()).block();
        updatedInteracao.dataHora(UPDATED_DATA_HORA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedInteracao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedInteracao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInteracaoToMatchAllProperties(updatedInteracao);
    }

    @Test
    void putNonExistingInteracao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interacao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, interacao.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInteracao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInteracao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInteracaoWithPatch() throws Exception {
        // Initialize the database
        insertedInteracao = interacaoRepository.save(interacao).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the interacao using partial update
        Interacao partialUpdatedInteracao = new Interacao();
        partialUpdatedInteracao.setId(interacao.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInteracao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInteracao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interacao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInteracaoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInteracao, interacao),
            getPersistedInteracao(interacao)
        );
    }

    @Test
    void fullUpdateInteracaoWithPatch() throws Exception {
        // Initialize the database
        insertedInteracao = interacaoRepository.save(interacao).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the interacao using partial update
        Interacao partialUpdatedInteracao = new Interacao();
        partialUpdatedInteracao.setId(interacao.getId());

        partialUpdatedInteracao.dataHora(UPDATED_DATA_HORA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInteracao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInteracao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interacao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInteracaoUpdatableFieldsEquals(partialUpdatedInteracao, getPersistedInteracao(partialUpdatedInteracao));
    }

    @Test
    void patchNonExistingInteracao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interacao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, interacao.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInteracao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInteracao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        interacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(interacao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInteracao() {
        // Initialize the database
        insertedInteracao = interacaoRepository.save(interacao).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the interacao
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, interacao.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return interacaoRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Interacao getPersistedInteracao(Interacao interacao) {
        return interacaoRepository.findById(interacao.getId()).block();
    }

    protected void assertPersistedInteracaoToMatchAllProperties(Interacao expectedInteracao) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInteracaoAllPropertiesEquals(expectedInteracao, getPersistedInteracao(expectedInteracao));
        assertInteracaoUpdatableFieldsEquals(expectedInteracao, getPersistedInteracao(expectedInteracao));
    }

    protected void assertPersistedInteracaoToMatchUpdatableProperties(Interacao expectedInteracao) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInteracaoAllUpdatablePropertiesEquals(expectedInteracao, getPersistedInteracao(expectedInteracao));
        assertInteracaoUpdatableFieldsEquals(expectedInteracao, getPersistedInteracao(expectedInteracao));
    }
}
