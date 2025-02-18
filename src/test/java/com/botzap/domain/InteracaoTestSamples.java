package com.botzap.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class InteracaoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Interacao getInteracaoSample1() {
        return new Interacao().id(1L);
    }

    public static Interacao getInteracaoSample2() {
        return new Interacao().id(2L);
    }

    public static Interacao getInteracaoRandomSampleGenerator() {
        return new Interacao().id(longCount.incrementAndGet());
    }
}
