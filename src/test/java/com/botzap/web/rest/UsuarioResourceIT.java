package com.botzap.web.rest;

import static com.botzap.domain.UsuarioAsserts.*;
import static com.botzap.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.botzap.IntegrationTest;
import com.botzap.domain.Usuario;
import com.botzap.repository.EntityManager;
import com.botzap.repository.UsuarioRepository;
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
 * Integration tests for the {@link UsuarioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UsuarioResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/usuarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Usuario usuario;

    private Usuario insertedUsuario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Usuario createEntity() {
        return new Usuario().nome(DEFAULT_NOME).telefone(DEFAULT_TELEFONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Usuario createUpdatedEntity() {
        return new Usuario().nome(UPDATED_NOME).telefone(UPDATED_TELEFONE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Usuario.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        usuario = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUsuario != null) {
            usuarioRepository.delete(insertedUsuario).block();
            insertedUsuario = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUsuario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Usuario
        var returnedUsuario = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Usuario.class)
            .returnResult()
            .getResponseBody();

        // Validate the Usuario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUsuarioUpdatableFieldsEquals(returnedUsuario, getPersistedUsuario(returnedUsuario));

        insertedUsuario = returnedUsuario;
    }

    @Test
    void createUsuarioWithExistingId() throws Exception {
        // Create the Usuario with an existing ID
        usuario.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        usuario.setNome(null);

        // Create the Usuario, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTelefoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        usuario.setTelefone(null);

        // Create the Usuario, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllUsuariosAsStream() {
        // Initialize the database
        usuarioRepository.save(usuario).block();

        List<Usuario> usuarioList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Usuario.class)
            .getResponseBody()
            .filter(usuario::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(usuarioList).isNotNull();
        assertThat(usuarioList).hasSize(1);
        Usuario testUsuario = usuarioList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertUsuarioAllPropertiesEquals(usuario, testUsuario);
        assertUsuarioUpdatableFieldsEquals(usuario, testUsuario);
    }

    @Test
    void getAllUsuarios() {
        // Initialize the database
        insertedUsuario = usuarioRepository.save(usuario).block();

        // Get all the usuarioList
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
            .value(hasItem(usuario.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].telefone")
            .value(hasItem(DEFAULT_TELEFONE));
    }

    @Test
    void getUsuario() {
        // Initialize the database
        insertedUsuario = usuarioRepository.save(usuario).block();

        // Get the usuario
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, usuario.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(usuario.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.telefone")
            .value(is(DEFAULT_TELEFONE));
    }

    @Test
    void getNonExistingUsuario() {
        // Get the usuario
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUsuario() throws Exception {
        // Initialize the database
        insertedUsuario = usuarioRepository.save(usuario).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the usuario
        Usuario updatedUsuario = usuarioRepository.findById(usuario.getId()).block();
        updatedUsuario.nome(UPDATED_NOME).telefone(UPDATED_TELEFONE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUsuario.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedUsuario))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUsuarioToMatchAllProperties(updatedUsuario);
    }

    @Test
    void putNonExistingUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        usuario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, usuario.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        usuario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        usuario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUsuarioWithPatch() throws Exception {
        // Initialize the database
        insertedUsuario = usuarioRepository.save(usuario).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the usuario using partial update
        Usuario partialUpdatedUsuario = new Usuario();
        partialUpdatedUsuario.setId(usuario.getId());

        partialUpdatedUsuario.nome(UPDATED_NOME).telefone(UPDATED_TELEFONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsuario.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUsuario))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Usuario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUsuarioUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUsuario, usuario), getPersistedUsuario(usuario));
    }

    @Test
    void fullUpdateUsuarioWithPatch() throws Exception {
        // Initialize the database
        insertedUsuario = usuarioRepository.save(usuario).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the usuario using partial update
        Usuario partialUpdatedUsuario = new Usuario();
        partialUpdatedUsuario.setId(usuario.getId());

        partialUpdatedUsuario.nome(UPDATED_NOME).telefone(UPDATED_TELEFONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsuario.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUsuario))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Usuario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUsuarioUpdatableFieldsEquals(partialUpdatedUsuario, getPersistedUsuario(partialUpdatedUsuario));
    }

    @Test
    void patchNonExistingUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        usuario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, usuario.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        usuario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        usuario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(usuario))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Usuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUsuario() {
        // Initialize the database
        insertedUsuario = usuarioRepository.save(usuario).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the usuario
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, usuario.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return usuarioRepository.count().block();
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

    protected Usuario getPersistedUsuario(Usuario usuario) {
        return usuarioRepository.findById(usuario.getId()).block();
    }

    protected void assertPersistedUsuarioToMatchAllProperties(Usuario expectedUsuario) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUsuarioAllPropertiesEquals(expectedUsuario, getPersistedUsuario(expectedUsuario));
        assertUsuarioUpdatableFieldsEquals(expectedUsuario, getPersistedUsuario(expectedUsuario));
    }

    protected void assertPersistedUsuarioToMatchUpdatableProperties(Usuario expectedUsuario) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUsuarioAllUpdatablePropertiesEquals(expectedUsuario, getPersistedUsuario(expectedUsuario));
        assertUsuarioUpdatableFieldsEquals(expectedUsuario, getPersistedUsuario(expectedUsuario));
    }
}
