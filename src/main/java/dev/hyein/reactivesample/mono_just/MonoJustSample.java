package dev.hyein.reactivesample.mono_just;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoJustSample {

    public void monoJust() throws InterruptedException {
        Mono.just(oneSecond())
            .flatMap( s ->
                {
                    try {
                        return Mono.zip(
                            Mono.just(twoSecond()),
                            Mono.just(threeSecond())
                        );
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            )
            .subscribe(tuple -> log.info("tuple : {}", tuple))
        ;
    }

    private Mono<String> oneSecond() throws InterruptedException {
        log.info("[just] 시작");
        Thread.sleep(1000);
        log.info("[just] 종료");

        String value = "[just] 1초";
        log.info(value);
        return Mono.just(value);
    }

    private String twoSecond(Mono<String> s) throws InterruptedException {
        log.info("[twoSecond] 시작");
        Thread.sleep(2000);
        log.info("[twoSecond] 종료");

        String value = String.format("%s [twoSecond] 2초", s);
        log.info(value);
        return value;
    }

    private String threeSecond(Mono<String> s) throws InterruptedException {
        log.info("[threeSecond] 시작");
        Thread.sleep(3000);
        log.info("[threeSecond] 종료");

        String value = String.format("%s [threeSecond] 3초", s);
        log.info(value);
        return value;
    }

    private String twoSecond() throws InterruptedException {
        log.info("[twoSecond] 시작");
        Thread.sleep(2000);
        log.info("[twoSecond] 종료");

        String value = String.format("[twoSecond] 2초");
        log.info(value);
        return value;
    }

    private String threeSecond() throws InterruptedException {
        log.info("[threeSecond] 시작");
        Thread.sleep(3000);
        log.info("[threeSecond] 종료");

        String value = String.format("[threeSecond] 3초");
        log.info(value);
        return value;
    }
}
