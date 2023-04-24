package dev.hyein.reactivesample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class CallBackHellCustomizing {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("start");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(20);
        threadPoolTaskExecutor.initialize();

        ListenableFuture<IntStream> future = getListenableFuture1(threadPoolTaskExecutor);

        future.addCallback(s -> {
            log.info("service 1 callback success:{}", s.sum());

            ListenableFuture<IntStream> future2 = getListenableFuture2(threadPoolTaskExecutor, s);
            future2.addCallback(s2 -> {
                log.info("service 2 callback success:{}", s2);

                ListenableFuture<List<String>> future3 = getListenableFuture3(threadPoolTaskExecutor, s2);
                future3.addCallback(s3 -> {
                    log.info("s3 {}", s3);
                }, e -> log.error(e.getMessage()));
            }, e -> log.error(e.getMessage()));

        }, e -> log.error(e.getMessage()));

//        threadPoolTaskExecutor.shutdown();
        log.info("end");
    }

    private static ListenableFuture<IntStream> getListenableFuture1(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return threadPoolTaskExecutor.submitListenable(() -> {
            log.info("{} wait start", "serviceName1");
            Thread.sleep(1000);
            log.info("{} wait end", "serviceName1");
            return IntStream.rangeClosed(1, 10);
        });
    }

    private static ListenableFuture<IntStream> getListenableFuture2(ThreadPoolTaskExecutor threadPoolTaskExecutor, IntStream s) {
        return threadPoolTaskExecutor.submitListenable(() -> {
            log.info("{} wait start", "serviceName2");
            Thread.sleep(1000);
            log.info("{} wait end", "serviceName2");
            IntStream intStream = s.map(i -> i * 10);
            log.info("serv2 ooo");
            return intStream;
        });
    }

    private static ListenableFuture<List<String>> getListenableFuture3(ThreadPoolTaskExecutor threadPoolTaskExecutor, IntStream s2) {
        return threadPoolTaskExecutor.submitListenable(() -> {
            log.info("{} wait start", "serviceName3");
            Thread.sleep(1000);
            log.info("{} wait end", "serviceName3");
            return s2.boxed()
                .map(i -> "service3" + i)
                .collect(Collectors.toList());
        });
    }

}
