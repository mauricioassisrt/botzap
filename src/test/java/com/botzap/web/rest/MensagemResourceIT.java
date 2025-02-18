package com.botzap.web.rest;

import static com.botzap.domain.MensagemAsserts.*;
import static com.botzap.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.botzap.IntegrationTest;
import com.botzap.domain.Mensagem;
import com.botzap.repository.EntityManager;
import com.botzap.repository.MensagemRepository;
import com.botzap.service.MensagemService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link MensagemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MensagemResourceIT {

    private static final String DEFAULT_TEXTO = "AAAAAAAAAA";
    private static final String UPDATED_TEXTO = "BBBBBBBBBB";

    private static final Integer DEFAULT_OPCAO = 1;
    private static final Integer UPDATED_OPCAO = 2;

    private static final String ENTITY_API_URL = "/api/mensagems";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MensagemRepository mensagemRepository;

    @Mock
    private MensagemRepository mensagemRepositoryMock;

    @Mock
    private MensagemService mensagemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Mensagem mensagem;

    private Mensagem insertedMensagem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mensagem createEntity() {
        return new Mensagem().texto(DEFAULT_TEXTO).opcao(DEFAULT_OPCAO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mensagem createUpdatedEntity() {
        return new Mensagem().texto(UPDATED_TEXTO).opcao(UPDATED_OPCAO);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Mensagem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        mensagem = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMensagem != null) {
            mensagemRepository.delete(insertedMensagem).block();
            insertedMensagem = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMensagem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Mensagem
        var returnedMensagem = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Mensagem.class)
            .returnResult()
            .getResponseBody();

        // Validate the Mensagem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMensagemUpdatableFieldsEquals(returnedMensagem, getPersistedMensagem(returnedMensagem));

        insertedMensagem = returnedMensagem;
    }

    @Test
    void createMensagemWithExistingId() throws Exception {
        // Create the Mensagem with an existing ID
        mensagem.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTextoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mensagem.setTexto(null);

        // Create the Mensagem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMensagems() {
        // Initialize the database
        insertedMensagem = mensagemRepository.save(mensagem).block();

        // Get all the mensagemList
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
            .value(hasItem(mensagem.getId().intValue()))
            .jsonPath("$.[*].texto")
            .value(hasItem(DEFAULT_TEXTO))
            .jsonPath("$.[*].opcao")
            .value(hasItem(DEFAULT_OPCAO));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMensagemsWithEagerRelationshipsIsEnabled() {
        when(mensagemServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(mensagemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMensagemsWithEagerRelationshipsIsNotEnabled() {
        when(mensagemServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(mensagemRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMensagem() {
        // Initialize the database
        insertedMensagem = mensagemRepository.save(mensagem).block();

        // Get the mensagem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mensagem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mensagem.getId().intValue()))
            .jsonPath("$.texto")
            .value(is(DEFAULT_TEXTO))
            .jsonPath("$.opcao")
            .value(is(DEFAULT_OPCAO));
    }

    @Test
    void getNonExistingMensagem() {
        // Get the mensagem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMensagem() throws Exception {
        // Initialize the database
        insertedMensagem = mensagemRepository.save(mensagem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mensagem
        Mensagem updatedMensagem = mensagemRepository.findById(mensagem.getId()).block();
        updatedMensagem.texto(UPDATED_TEXTO).opcao(UPDATED_OPCAO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMensagem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedMensagem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMensagemToMatchAllProperties(updatedMensagem);
    }

    @Test
    void putNonExistingMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensagem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mensagem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMensagemWithPatch() throws Exception {
        // Initialize the database
        insertedMensagem = mensagemRepository.save(mensagem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mensagem using partial update
        Mensagem partialUpdatedMensagem = new Mensagem();
        partialUpdatedMensagem.setId(mensagem.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMensagem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMensagem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Mensagem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMensagemUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMensagem, mensagem), getPersistedMensagem(mensagem));
    }

    @Test
    void fullUpdateMensagemWithPatch() throws Exception {
        // Initialize the database
        insertedMensagem = mensagemRepository.save(mensagem).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mensagem using partial update
        Mensagem partialUpdatedMensagem = new Mensagem();
        partialUpdatedMensagem.setId(mensagem.getId());

        partialUpdatedMensagem.texto(UPDATED_TEXTO).opcao(UPDATED_OPCAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMensagem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMensagem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Mensagem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMensagemUpdatableFieldsEquals(partialUpdatedMensagem, getPersistedMensagem(partialUpdatedMensagem));
    }

    @Test
    void patchNonExistingMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensagem.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mensagem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMensagem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mensagem.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(mensagem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Mensagem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMensagem() {
        // Initialize the database
        insertedMensagem = mensagemRepository.save(mensagem).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mensagem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mensagem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mensagemRepository.count().block();
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

    protected Mensagem getPersistedMensagem(Mensagem mensagem) {
        return mensagemRepository.findById(mensagem.getId()).block();
    }

    protected void assertPersistedMensagemToMatchAllProperties(Mensagem expectedMensagem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMensagemAllPropertiesEquals(expectedMensagem, getPersistedMensagem(expectedMensagem));
        assertMensagemUpdatableFieldsEquals(expectedMensagem, getPersistedMensagem(expectedMensagem));
    }

    protected void assertPersistedMensagemToMatchUpdatableProperties(Mensagem expectedMensagem) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMensagemAllUpdatablePropertiesEquals(expectedMensagem, getPersistedMensagem(expectedMensagem));
        assertMensagemUpdatableFieldsEquals(expectedMensagem, getPersistedMensagem(expectedMensagem));
    }
}
