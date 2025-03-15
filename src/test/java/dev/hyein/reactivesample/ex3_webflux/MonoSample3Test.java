package dev.hyein.reactivesample.ex3_webflux;

import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class MonoSample3Test {

    @Test
    public void test1() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux).expectNext("foo", "bar").verifyComplete(); // 틀리면 AssertionError 남
    }

    @Test
    public void test2() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux).expectNext("foo", "bar").expectError(RuntimeException.class).verify(); // 틀리면 AssertionError 남
    }

    @Test
    public void test3() {
        Flux<User> flux = Flux.just(new User("swhite"), new User("jpinkman"));
        StepVerifier.create(flux)
            .assertNext(user -> assertThat(user.getUsername()).isEqualTo("swhite"))
            .assertNext(user -> assertThat(user.getUsername()).isEqualTo("jpinkman"))
            .verifyComplete();
    }

    @Test
    public void test4() {
        Flux<Long> flux = Flux.just(LongStream.range(0, 10).boxed().toArray(Long[]::new));
        Duration duration = StepVerifier.create(flux).expectNextCount(10).verifyComplete();
        System.out.println(duration.toMillis());
    }

    @Test
    public void test5() {
        Supplier<Flux<Long>> flux = () -> Flux.just(LongStream.range(0, 3600).boxed().toArray(Long[]::new)).delayElements(Duration.ofSeconds(1));
        Duration duration = StepVerifier.withVirtualTime(flux).thenAwait(Duration.ofHours(1)).expectNextCount(3600).verifyComplete();
        System.out.println(duration.toMillis());
    }

    @Test
    public void test6() {
        Flux.just(new User("A"))
            .flatMap(u -> Flux.just(new User(u.getUsername().toUpperCase())))
    .subscribe(System.out::println)
        ;
    }


    @Test
    public void test7() {
        Flux<String> flux1 = Flux.just("foo", "bar");
        Flux<String> flux2 = Flux.just("a", "b");

        Flux.merge(flux1, flux2).subscribe(System.out::println);
        Flux.concat(flux1, flux2).subscribe(System.out::println);

        Mono<String> mono1 = Mono.just("foo");
        Mono<String> mono2 = Mono.just("a");
        Flux.concat(mono1, mono2).subscribe(System.out::println);
    }

    @Test
    public void test8() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux).thenRequest(Long.MAX_VALUE).expectNextCount(4L).expectComplete();

        StepVerifier.create(flux).expectNext("foo").expectNext("bar").thenCancel();
        flux.log().subscribe(System.out::println);

        flux.doFirst(() -> System.out.println("Starring:")).doOnNext(s -> System.out.println(s)).doOnComplete(() -> System.out.println("The end!")).subscribe(System.out::println);
    }

    @Test
    public void test9() {
        Mono<String> mono = Mono.just( "bar");
        mono.onErrorReturn(IllegalStateException.class, "");

        Flux<String> flux = Flux.just("foo", "bar");
        flux.onErrorResume(Exception.class, e -> Flux.just(""));
        flux.map(s -> {
            try {
                if(1 == 1) throw new RuntimeException();
                return "B";
            } catch (RuntimeException e) {
                throw Exceptions.propagate(e);
            }
        });
    }

    @Test
    public void test10() {
        Flux<String> flux1 = Flux.just("foo", "bar");
        Flux<String> flux2 = Flux.just("a", "b");
        Flux<String> flux3 = Flux.just("ss", "vv");

        Flux.zip(flux1, flux2, flux3).map(t -> new User(t.getT1()));


        Mono<String> mono1 = Mono.just( "foo");
        Mono<String> mono2 = Mono.just( "bar");
        Mono.firstWithValue(mono1, mono2);

        flux1.ignoreElements().then();

        Mono.justOrEmpty(null).switchIfEmpty(Mono.just(new User("s"))).subscribe(System.out::println);

        flux1.collectList();
        flux1.toIterable();
    }

    @Test
    public void test11() {
        Flux.defer(() -> Flux.just("foo", "bar")).subscribeOn(Schedulers.boundedElastic());
        Flux.just("foo", "bar").publishOn(Schedulers.boundedElastic()).doOnNext(s -> System.out.println(s)).then();
    }

}
