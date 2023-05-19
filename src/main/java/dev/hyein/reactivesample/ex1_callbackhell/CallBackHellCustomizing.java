package dev.hyein.reactivesample.ex1_callbackhell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class CallBackHellCustomizing {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("start");
        ThreadPoolTaskExecutor executor = getThreadPoolTaskExecutor();

        callbackHell(executor);

        // 해결 1
//        solution1(executor);
        // 해결 2
//        solution2(executor);
        // 해결 3
//        solution3();

        log.info("end");

        executor.getThreadPoolExecutor().awaitTermination(Duration.ofSeconds(3).getSeconds(), TimeUnit.SECONDS);
        executor.shutdown();

    }

    private static ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    private static void callbackHell(ThreadPoolTaskExecutor executor) {
        ListenableFuture<IntStream> future1 = getListenableFuture1(executor);
        future1.addCallback(s -> {
            log.info("service 1 success");

            ListenableFuture<IntStream> future2 = getListenableFuture2(executor, s);
            future2.addCallback(s2 -> {
                log.info("service 2 success");

                ListenableFuture<List<String>> future3 = getListenableFuture3(executor, s2);
                log.info("service 3 success");

                future3.addCallback(s3 -> {
                    log.info("Finished: {}", s3);
                }, e -> log.error(e.getMessage()));
            }, e -> log.error(e.getMessage()));

        }, e -> log.error(e.getMessage()));
    }

    private static void solution1(ThreadPoolTaskExecutor executor) {
        Completion.from(getListenableFuture1(executor))
                .andApply(intStream -> getListenableFuture2(executor, (IntStream) intStream))
                .andApply(intStream -> getListenableFuture3(executor, (IntStream) intStream))
                .andAccept(intList -> log.info("Finished: {}", intList))
        ;
    }

    private static void solution2(ThreadPoolTaskExecutor executor) {
        CompletableFuture
                .supplyAsync(() -> IntStream.rangeClosed(1, 10), executor)
                .thenApply(s -> s.map(i -> i * 10))
                .thenApply(s -> s.boxed().map(i -> "work " + i).collect(Collectors.toList()))
                .thenAccept(s -> log.info("Finished: {}", s))
        ;
    }

    private static void solution3() {
        Mono.just(IntStream.rangeClosed(1, 10))
                .map(s -> s.map(i -> i * 10))
                .map(s -> s.boxed().map(i -> "service3:" + i).collect(Collectors.toList()))
                .subscribe(s -> log.info("result: {}", s));
    }

    private static ListenableFuture<IntStream> getListenableFuture1(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return threadPoolTaskExecutor.submitListenable(() -> {
            Thread.sleep(100);
            return IntStream.rangeClosed(1, 10);
        });
    }

    private static ListenableFuture<IntStream> getListenableFuture2(ThreadPoolTaskExecutor threadPoolTaskExecutor, IntStream s) {
        return threadPoolTaskExecutor.submitListenable(() -> s.map(i -> i * 10));
    }

    private static ListenableFuture<List<String>> getListenableFuture3(ThreadPoolTaskExecutor threadPoolTaskExecutor, IntStream s2) {
        return threadPoolTaskExecutor.submitListenable(() ->
                 s2.boxed()
                .map(i -> "work " + i)
                .collect(Collectors.toList())
        );
    }
}
