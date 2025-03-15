package dev.hyein.reactivesample.mono_just;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonoJustSampleTest {
    @Test
    void just() {
        MonoJustSample monoJustSample = new MonoJustSample();
        try {
            monoJustSample.monoJust();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
