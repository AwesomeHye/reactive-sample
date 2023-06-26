package dev.hyein.reactivesample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;


@Slf4j
public class ErrorTest {
    @Test
    public void doOnError() {
        Flux.range(0, 5)
            .map(num -> {

                if (num == 3) {
                    throw new RuntimeException("Num is " + num);
                }

                return num;
            })
            .doOnError(throwable -> log.error("err : {}", throwable.getMessage())) // 에러로그도 찍히고
            .onErrorReturn(100) // 100도 반환함
            .log()
            .subscribe();
    }

}
