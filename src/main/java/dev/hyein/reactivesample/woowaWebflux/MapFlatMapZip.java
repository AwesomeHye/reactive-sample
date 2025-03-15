package dev.hyein.reactivesample.woowaWebflux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;

@Slf4j
public class MapFlatMapZip {

    public void zip1() {
        log.info("== zip1 ==");
        Mono.zip(get(1), get(2))
            .flatMap(value -> {
                log.info("flatMap called: {}", value); // [1,2]
                return Mono.just(value.getT1() * 100);
            }).subscribe(value -> log.info("subscribe called: {}", value)) // 100
        ;
    }

    public void zip2() {
        log.info("== zip2 ==");
        Mono.zip(get(1), get(2))
            .map(value -> {
                log.info("map called: {}", value); // [1,2]
                return Mono.just(value.getT1() * 100);
            }).subscribe(value -> log.info("subscribe called: {}", value)) // MonoJust
        ;
    }

    public void mono1() {
        log.info("== mono1 ==");
        get(1)
            .flatMap(value -> { // mono 를 한 번 까기 때문에 integer 를 반환한다.
                log.info("flatMap called: {}", value); // 1
                return Mono.just(value * 100);
            }) // flatMap 은 return 값은 Mono 로 한 번 더 감싼지만 평탄화하기 때문에 Mono 를 반환한다.
            .subscribe(value -> log.info("subscribe called: {}", value)) // 100
        ;
    }

    public void mono2() {
        log.info("== mono2 ==");
        get(1)
            .map(value -> { // mono 를 한 번 까기 때문에 integer 를 반환한다.
                log.info("map called: {}", value); // 1
                return Mono.just(value * 100);
            }) // map 은 return 값은 Mono 로 한 번 더 감싸기 때문에 MonoJust 를 출력한다.
            .subscribe(value -> log.info("subscribe called: {}", value)) // MonoJust
        ;
    }

    private Mono<Integer> get(int value) {
        return Mono.just(value);
    }

    public void mix1() {
        log.info("== mix1 ==");
        Mono<Tuple2<Integer, Integer>> map =
            Mono.zip(get(1), get(2)) // Mono.zip(get(1), get(2))는 두 개의 Mono<Integer>를 결합하여 Mono<Tuple2<Integer, Integer>>를 생성
                .flatMap(tuple -> { // Tuple2<Integer, Integer>. flatMap, map 둘 다 까지는 건 똑같음
                    log.info("flatMap called: {}", tuple); // [1,2]
                    return Mono.zip(get(tuple.getT1() * 10), get(tuple.getT2() * 10)); // Mono<Integer>를 결합하여 Mono<Tuple2<Integer, Integer>>를 반환
                })
                .map(tuple -> { // Tuple2<Integer, Integer>. map 과 다름
                    log.info("map called: {}", tuple); // [10,20]
                    return Tuples.of(tuple.getT1(), tuple.getT2());
                }) // map 이 return 값을 Mono 로 감싸기 때문에 반환값이 Mono 이다.
            ;

        map
            .subscribe(value -> log.info("subscribe called: {}", value)) // [10,20]
        ;
    }

    public void mix2() {
        log.info("== mix2 ==");
        Mono<Tuple2<Mono<Integer>, Mono<Integer>>> map =
            Mono.zip(get(1), get(2))
                .map(tuple -> { // Tuple2<Integer, Integer>. flatMap, map 둘 다 까지는 건 똑같음
                    log.info("map called: {}", tuple); // [1,2]
                    return Tuples.of(get(tuple.getT1() * 10), get(tuple.getT2() * 10));
                })
                .map(tuple -> { // Tuple2<Mono<Integer>, Mono<Integer>>. flatMap 과 다름
                    log.info("map called: {}", tuple);  // [MonoJust, MonoJust]
                    return Tuples.of(tuple.getT1(), tuple.getT2());
                });

        map
            .subscribe(value -> log.info("subscribe called: {}", value)) // [MonoJust, MonoJust]
        ;
    }

    public void flatMapMany() {
        log.info("== flatMapMany ==");

        Flux<Integer> flux = Mono.just(1)
            .flatMapMany(value -> {
                log.info("flatMapMany called: {}", value);
                return Flux.range(0, value + 10);
            });

        flux
            .subscribe(
                value -> log.info("subscribe called: {}", value),
                error -> log.error("error: {}", error.getMessage(), error),
                () -> log.info("onComplete")
            );
    }

    public void flatMapSequential() {
        log.info("== flatMapSequential ==");

        Flux<Integer> flux = Flux.just(1, 2, 3)
            .flatMapSequential(value -> { // concatMap, flatMap
                if (value == 1) {
                    return Mono.just(value)
                        .doOnNext(v -> log.info("[{}] delay starting", v))
                        .delayElement(Duration.ofSeconds(5))
                        .doOnNext(v -> log.info("[{}] finished", v));
                } else if (value == 2) {
                    return Mono.just(value)
                        .doOnNext(v -> log.info("[{}] delay starting", v))
                        .delayElement(Duration.ofSeconds(3))
                        .doOnNext(v -> log.info("[{}] finished", v));

                } else if (value == 3) {
                    return Mono.just(value)
                        .doOnNext(v -> log.info("[{}] delay starting", v))
                        .delayElement(Duration.ofSeconds(1))
                        .doOnNext(v -> log.info("[{}] finished", v));
                }

                return Mono.just(value);
            });

        flux
            .subscribeOn(Schedulers.boundedElastic()) // 스레드풀 제한두려고
            .doOnNext(value -> log.info("Done: {}", value))
            .blockLast(); // 안하면 바로 끝남
    }


}
