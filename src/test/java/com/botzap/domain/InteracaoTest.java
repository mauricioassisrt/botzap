package com.botzap.domain;

import static com.botzap.domain.InteracaoTestSamples.*;
import static com.botzap.domain.MensagemTestSamples.*;
import static com.botzap.domain.UsuarioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.botzap.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InteracaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Interacao.class);
        Interacao interacao1 = getInteracaoSample1();
        Interacao interacao2 = new Interacao();
        assertThat(interacao1).isNotEqualTo(interacao2);

        interacao2.setId(interacao1.getId());
        assertThat(interacao1).isEqualTo(interacao2);

        interacao2 = getInteracaoSample2();
        assertThat(interacao1).isNotEqualTo(interacao2);
    }

    @Test
    void usuarioTest() {
        Interacao interacao = getInteracaoRandomSampleGenerator();
        Usuario usuarioBack = getUsuarioRandomSampleGenerator();

        interacao.setUsuario(usuarioBack);
        assertThat(interacao.getUsuario()).isEqualTo(usuarioBack);

        interacao.usuario(null);
        assertThat(interacao.getUsuario()).isNull();
    }

    @Test
    void mensagemTest() {
        Interacao interacao = getInteracaoRandomSampleGenerator();
        Mensagem mensagemBack = getMensagemRandomSampleGenerator();

        interacao.setMensagem(mensagemBack);
        assertThat(interacao.getMensagem()).isEqualTo(mensagemBack);

        interacao.mensagem(null);
        assertThat(interacao.getMensagem()).isNull();
    }
}
