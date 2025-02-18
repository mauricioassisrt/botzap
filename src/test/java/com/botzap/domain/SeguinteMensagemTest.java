package com.botzap.domain;

import static com.botzap.domain.MensagemTestSamples.*;
import static com.botzap.domain.SeguinteMensagemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.botzap.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SeguinteMensagemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SeguinteMensagem.class);
        SeguinteMensagem seguinteMensagem1 = getSeguinteMensagemSample1();
        SeguinteMensagem seguinteMensagem2 = new SeguinteMensagem();
        assertThat(seguinteMensagem1).isNotEqualTo(seguinteMensagem2);

        seguinteMensagem2.setId(seguinteMensagem1.getId());
        assertThat(seguinteMensagem1).isEqualTo(seguinteMensagem2);

        seguinteMensagem2 = getSeguinteMensagemSample2();
        assertThat(seguinteMensagem1).isNotEqualTo(seguinteMensagem2);
    }

    @Test
    void destinoTest() {
        SeguinteMensagem seguinteMensagem = getSeguinteMensagemRandomSampleGenerator();
        Mensagem mensagemBack = getMensagemRandomSampleGenerator();

        seguinteMensagem.addDestino(mensagemBack);
        assertThat(seguinteMensagem.getDestinos()).containsOnly(mensagemBack);
        assertThat(mensagemBack.getFluxo()).isEqualTo(seguinteMensagem);

        seguinteMensagem.removeDestino(mensagemBack);
        assertThat(seguinteMensagem.getDestinos()).doesNotContain(mensagemBack);
        assertThat(mensagemBack.getFluxo()).isNull();

        seguinteMensagem.destinos(new HashSet<>(Set.of(mensagemBack)));
        assertThat(seguinteMensagem.getDestinos()).containsOnly(mensagemBack);
        assertThat(mensagemBack.getFluxo()).isEqualTo(seguinteMensagem);

        seguinteMensagem.setDestinos(new HashSet<>());
        assertThat(seguinteMensagem.getDestinos()).doesNotContain(mensagemBack);
        assertThat(mensagemBack.getFluxo()).isNull();
    }
}
