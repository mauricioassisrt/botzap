package com.botzap.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SeguinteMensagemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SeguinteMensagem getSeguinteMensagemSample1() {
        return new SeguinteMensagem().id(1L).descricao("descricao1");
    }

    public static SeguinteMensagem getSeguinteMensagemSample2() {
        return new SeguinteMensagem().id(2L).descricao("descricao2");
    }

    public static SeguinteMensagem getSeguinteMensagemRandomSampleGenerator() {
        return new SeguinteMensagem().id(longCount.incrementAndGet()).descricao(UUID.randomUUID().toString());
    }
}
