package dev.hyein.reactivesample.coldhot;

import reactor.core.publisher.Flux;

import java.util.Arrays;

public class ColdSequence {

    public static void main(String[] args) throws InterruptedException {
        Flux<String> flux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"));

        flux.subscribe(System.out::println);

        Thread.sleep(2000);

        flux.subscribe(System.out::println);
    }

}
