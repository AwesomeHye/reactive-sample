package dev.hyein.reactivesample.ex3_webflux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MonoSample2 {

    /**
     * upstream 데이터를 downstream 연이어 내려가는 방법: zipWhen
     * @param args
     */
    public static void main(String[] args) {
        Mono<String> a1 = getLongA1();
        Mono<String> a2 = Mono.just("A2");
        Mono<String> b1 = Mono.just("B1");

        log.info("START");
        Mono.zip(a1, a2)
            .zipWhen(t -> Mono.zip(b1, getB2(t.getT2())))
            .doOnNext(t2 -> log.info("zipWhen1(2초짜리): {} ", t2))
            .zipWhen(t2 -> Mono.zip(getC1(t2.getT2().getT1()), getC2(t2.getT1().getT2())))
            .doOnNext(t2 -> log.info("zipWhen2(3초짜리): {}", t2))
            .map(t2 -> getD1(t2.getT1().getT2().getT1(), t2.getT2().getT1(), t2.getT2().getT2()))
            .subscribe(mono -> log.info(mono.block()));
    }

    private static Mono<String> getLongA1() {
        try {
            log.info("getLongA1() START");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Mono.just("A1");
    }

    public static Mono<String> getB2(String a2) {
        return Mono.just(a2 + " B2");
    }

    public static Mono<String> getC1(String b1) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Mono.just(b1 + " C1");
    }

    public static Mono<String> getC2(String a2) {
        return Mono.just(a2 + " C2");
    }

    public static Mono<String> getD1(String b1, String c1, String c2) {
        return Mono.just(b1 + c1 + c2 + " D1");
    }
}
