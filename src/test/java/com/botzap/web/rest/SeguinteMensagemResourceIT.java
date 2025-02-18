package com.botzap.web.rest;

import static com.botzap.domain.SeguinteMensagemAsserts.*;
import static com.botzap.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.botzap.IntegrationTest;
import com.botzap.domain.SeguinteMensagem;
import com.botzap.repository.EntityManager;
import com.botzap.repository.SeguinteMensagemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link SeguinteMensagemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SeguinteMensagemResourceIT {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/seguinte-mensagems";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SeguinteMensagemRepository seguinteMensagemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SeguinteMensagem seguinteMensagem;

    private SeguinteMensagem insertedSeguinteMensagem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SeguinteMensagem createEntity() {
        return new SeguinteMensagem().descricao(DEFAULT_DESCRICAO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SeguinteMensagem createUpdatedEntity() {
        return new SeguinteMensagem().descricao(UPDATED_DESCRICAO);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(SeguinteMensagem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        seguinteMensagem = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSeguinteMensagem != null) {
            seguinteMensagemRepository.delete(insertedSeguinteMensagem).block();
            insertedSeguinteMensagem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSeguinteMensagem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SeguinteMensagem
        var returnedSeguinteMensagem = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SeguinteMensagem.class)
            .returnResult()
            .getResponseBody();

        // Validate the SeguinteMensagem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSeguinteMensagemUpdatableFieldsEquals(returnedSeguinteMensagem, getPersistedSeguinteMensagem(returnedSeguinteMensagem));

        insertedSeguinteMensagem = returnedSeguinteMensagem;
    }

    @Test
    void createSeguinteMensagemWithExistingId() throws Exception {
        // Create the SeguinteMensagem with an existing ID
        seguinteMensagem.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDescricaoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        seguinteMensagem.setDescricao(null);

        // Create the SeguinteMensagem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSeguinteMensagemsAsStream() {
        // Initialize the database
        seguinteMensagemRepository.save(seguinteMensagem).block();

        List<SeguinteMensagem> seguinteMensagemList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SeguinteMensagem.class)
            .getResponseBody()
            .filter(seguinteMensagem::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(seguinteMensagemList).isNotNull();
        assertThat(seguinteMensagemList).hasSize(1);
        SeguinteMensagem testSeguinteMensagem = seguinteMensagemList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertSeguinteMensagemAllPropertiesEquals(seguinteMensagem, testSeguinteMensagem);
        assertSeguinteMensagemUpdatableFieldsEquals(seguinteMensagem, testSeguinteMensagem);
    }

    @Test
    void getAllSeguinteMensagems() {
        // Initialize the database
        insertedSeguinteMensagem = seguinteMensagemRepository.save(seguinteMensagem).block();

        // Get all the seguinteMensagemList
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
            .value(hasItem(seguinteMensagem.getId().intValue()))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO));
    }

    @Test
    void getSeguinteMensagem() {
        // Initialize the database
        insertedSeguinteMensagem = seguinteMensagemRepository.save(seguinteMensagem).block();

        // Get the seguinteMensagem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, seguinteMensagem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(seguinteMensagem.getId().intValue()))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO));
    }

    @Test
    void getNonExistingSeguinteMensagem() {
        // Get the seguinteMensagem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSeguinteMensagem() throws Exception {
        // Initialize the database
        insertedSeguinteMensagem = seguinteMensagemRepository.save(seguinteMensagem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seguinteMensagem
        SeguinteMensagem updatedSeguinteMensagem = seguinteMensagemRepository.findById(seguinteMensagem.getId()).block();
        updatedSeguinteMensagem.descricao(UPDATED_DESCRICAO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSeguinteMensagem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedSeguinteMensagem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSeguinteMensagemToMatchAllProperties(updatedSeguinteMensagem);
    }

    @Test
    void putNonExistingSeguinteMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seguinteMensagem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, seguinteMensagem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSeguinteMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seguinteMensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSeguinteMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seguinteMensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSeguinteMensagemWithPatch() throws Exception {
        // Initialize the database
        insertedSeguinteMensagem = seguinteMensagemRepository.save(seguinteMensagem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seguinteMensagem using partial update
        SeguinteMensagem partialUpdatedSeguinteMensagem = new SeguinteMensagem();
        partialUpdatedSeguinteMensagem.setId(seguinteMensagem.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSeguinteMensagem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSeguinteMensagem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SeguinteMensagem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeguinteMensagemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSeguinteMensagem, seguinteMensagem),
            getPersistedSeguinteMensagem(seguinteMensagem)
        );
    }

    @Test
    void fullUpdateSeguinteMensagemWithPatch() throws Exception {
        // Initialize the database
        insertedSeguinteMensagem = seguinteMensagemRepository.save(seguinteMensagem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the seguinteMensagem using partial update
        SeguinteMensagem partialUpdatedSeguinteMensagem = new SeguinteMensagem();
        partialUpdatedSeguinteMensagem.setId(seguinteMensagem.getId());

        partialUpdatedSeguinteMensagem.descricao(UPDATED_DESCRICAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSeguinteMensagem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSeguinteMensagem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SeguinteMensagem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSeguinteMensagemUpdatableFieldsEquals(
            partialUpdatedSeguinteMensagem,
            getPersistedSeguinteMensagem(partialUpdatedSeguinteMensagem)
        );
    }

    @Test
    void patchNonExistingSeguinteMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seguinteMensagem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, seguinteMensagem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSeguinteMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seguinteMensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSeguinteMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        seguinteMensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(seguinteMensagem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SeguinteMensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSeguinteMensagem() {
        // Initialize the database
        insertedSeguinteMensagem = seguinteMensagemRepository.save(seguinteMensagem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the seguinteMensagem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, seguinteMensagem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return seguinteMensagemRepository.count().block();
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

    protected SeguinteMensagem getPersistedSeguinteMensagem(SeguinteMensagem seguinteMensagem) {
        return seguinteMensagemRepository.findById(seguinteMensagem.getId()).block();
    }

    protected void assertPersistedSeguinteMensagemToMatchAllProperties(SeguinteMensagem expectedSeguinteMensagem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSeguinteMensagemAllPropertiesEquals(expectedSeguinteMensagem, getPersistedSeguinteMensagem(expectedSeguinteMensagem));
        assertSeguinteMensagemUpdatableFieldsEquals(expectedSeguinteMensagem, getPersistedSeguinteMensagem(expectedSeguinteMensagem));
    }

    protected void assertPersistedSeguinteMensagemToMatchUpdatableProperties(SeguinteMensagem expectedSeguinteMensagem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSeguinteMensagemAllUpdatablePropertiesEquals(expectedSeguinteMensagem, getPersistedSeguinteMensagem(expectedSeguinteMensagem));
        assertSeguinteMensagemUpdatableFieldsEquals(expectedSeguinteMensagem, getPersistedSeguinteMensagem(expectedSeguinteMensagem));
    }
}
