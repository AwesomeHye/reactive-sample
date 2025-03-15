package dev.hyein.reactivesample.woowaWebflux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class DoOn {

    public void success() {
        Flux.just(1, 2, 3)
            .doOnNext(i -> log.info("doOnNext: {}", i))
            .doOnComplete(() -> log.info("doOnComplete"))
            .doOnTerminate(() -> log.info("doOnTerminate"))
            .doAfterTerminate(() -> log.info("doAfterTerminate"))
            .doFinally(signalType -> log.info("doFinally: {}", signalType))
            .doOnError(e -> log.error("doOnError: {}", e.getMessage()))
            .subscribe();
    }

    public void fail() {
        Flux.just(1, 2, 0)
            .doOnNext(i -> {
                int div = 10 / i;
                log.info("doOnNext: {}", div);
            })
            .doOnComplete(() -> log.info("doOnComplete")) // 성공하면
            .doOnTerminate(() -> log.info("doOnTerminate")) // 성공이든 실패든 종료하면
            .doAfterTerminate(() -> log.info("doAfterTerminate")) // 종료 후에
            .doFinally(signalType -> log.info("doFinally: {}", signalType))
            .doOnError(e -> log.error("doOnError: {}", e.getMessage()))
            .subscribe();
    }
}
