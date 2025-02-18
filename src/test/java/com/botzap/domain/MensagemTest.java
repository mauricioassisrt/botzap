package com.botzap.domain;

import static com.botzap.domain.MensagemTestSamples.*;
import static com.botzap.domain.SeguinteMensagemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.botzap.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MensagemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mensagem.class);
        Mensagem mensagem1 = getMensagemSample1();
        Mensagem mensagem2 = new Mensagem();
        assertThat(mensagem1).isNotEqualTo(mensagem2);

        mensagem2.setId(mensagem1.getId());
        assertThat(mensagem1).isEqualTo(mensagem2);

        mensagem2 = getMensagemSample2();
        assertThat(mensagem1).isNotEqualTo(mensagem2);
    }

    @Test
    void fluxoTest() {
        Mensagem mensagem = getMensagemRandomSampleGenerator();
        SeguinteMensagem seguinteMensagemBack = getSeguinteMensagemRandomSampleGenerator();

        mensagem.setFluxo(seguinteMensagemBack);
        assertThat(mensagem.getFluxo()).isEqualTo(seguinteMensagemBack);

        mensagem.fluxo(null);
        assertThat(mensagem.getFluxo()).isNull();
    }
}
