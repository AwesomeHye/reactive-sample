package dev.hyein.reactivesample.woowaWebflux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

/**
 * just()
 * fromCallable() //  만약 예외가 발생하는 코드 호출시, defer는 개발자가 스스로 이를 잡는 코드를 작성해야 하지만, fromCallable의 경우에는 내부적으로 Mono.error를 호출하게 됩니다.
 * defer()
 * create()
 */
@Slf4j
public class Emit {

    public void just() throws InterruptedException {
        log.info("[just] 시작 시간: {}", LocalDateTime.now());
        Mono<LocalDateTime> just = Mono.just(LocalDateTime.now());
        Thread.sleep(2000);
        just.subscribe(localDateTime -> log.info("[just] 방출 시간: {}", localDateTime.toString())); // 선언 시점에 emit
    }

    public void defer() throws InterruptedException {
        log.info("[defer] 시작 시간: {}", LocalDateTime.now());
        Mono<LocalDateTime> defer = Mono.defer(() -> Mono.just(LocalDateTime.now()));
        Thread.sleep(2000);
        defer.subscribe(localDateTime -> log.info("[defer] 방출 시간: {}", localDateTime.toString())); // 구독 시점에 emit
    }

    public void fromCallable() throws InterruptedException {
        log.info("[fromCallable] 시작 시간: {}", LocalDateTime.now());
        Mono<LocalDateTime> from = Mono.fromCallable(() -> LocalDateTime.now()); // fromCallable 이 mono 로 감싸줌
        Thread.sleep(2000);
        from.subscribe(localDateTime -> log.info("[fromCallable] 방출 시간: {}", localDateTime.toString())); // 구독 시점에 emit
    }

    public void create() throws InterruptedException {
        log.info("[create] 시작 시간: {}", LocalDateTime.now());

        Mono<LocalDateTime> create = Mono.create(sink -> { // sink 로 관리
            sink.success(LocalDateTime.now());
        });
        Thread.sleep(2000);
        create.subscribe(localDateTime -> log.info("[create] 방출 시간: {}", localDateTime.toString())); // 구독 시점에 emit
    }

    public void fromCallableScheduler() throws InterruptedException {
        log.info("[fromCallableScheduler] 시작 시간: {}", LocalDateTime.now());
        Mono<LocalDateTime> from = Mono.fromCallable(() -> LocalDateTime.now()); // fromCallable 이 mono 로 감싸줌
        Thread.sleep(2000);
        from.subscribeOn(Schedulers.boundedElastic())
            .subscribe(localDateTime -> log.info("[fromCallableScheduler] 방출 시간1: {}", localDateTime.toString())); // boundedElastic-1 스레드에서 실행된다.
        Thread.sleep(2000);
        from.subscribe(localDateTime -> log.info("[fromCallableScheduler] 방출 시간2: {}", localDateTime.toString())); // 구독 시점에 emit
    }
}
