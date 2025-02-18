package com.botzap.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MensagemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Mensagem getMensagemSample1() {
        return new Mensagem().id(1L).texto("texto1").opcao(1);
    }

    public static Mensagem getMensagemSample2() {
        return new Mensagem().id(2L).texto("texto2").opcao(2);
    }

    public static Mensagem getMensagemRandomSampleGenerator() {
        return new Mensagem().id(longCount.incrementAndGet()).texto(UUID.randomUUID().toString()).opcao(intCount.incrementAndGet());
    }
}
