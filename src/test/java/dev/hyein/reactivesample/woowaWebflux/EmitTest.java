package dev.hyein.reactivesample.woowaWebflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class EmitTest {

    Emit emit = new Emit();

    @Test
    void emit() throws InterruptedException {
//        emit.just();
//        emit.defer();
//        emit.fromCallable();
//        emit.create();
        emit.fromCallableScheduler();
    }
}
