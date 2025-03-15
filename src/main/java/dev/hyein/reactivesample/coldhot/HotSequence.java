package dev.hyein.reactivesample.coldhot;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class HotSequence {

    public static void main(String[] args) throws InterruptedException {
        Flux<String> flux = Flux.just("A", "B", "C", "D", "E")
            .delayElements(java.time.Duration.ofSeconds(1))
            .share();

        flux.subscribe(emit -> log.info("[1] {}", emit));

        Thread.sleep(2500);

        flux.subscribe(emit -> log.info("[2] {}", emit));

        Thread.sleep(3000);

    }
}
