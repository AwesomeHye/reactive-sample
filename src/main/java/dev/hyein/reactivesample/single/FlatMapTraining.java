package dev.hyein.reactivesample.single;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class FlatMapTraining {

    public static void main(String[] args) {
        Flux.just("good", "bad")
            .flatMap(feeling -> Flux.just("Morning", "Afternoon", "Evening").map(time-> feeling + " " + time))
            .subscribe(log::info);
    }
}
