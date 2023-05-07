package dev.hyein.reactivesample.custom;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Slf4j
public class Completion<T> {

    Completion<T> next;
    Function<T, ListenableFuture<T>> function;
    Consumer<T> consumer;

    public Completion() {}
    public Completion(Function<T, ListenableFuture<T>> function) {
        this.function = function;
    }
    public Completion(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public static <T> Completion from(ListenableFuture<T> future) {
        // 첫 번째 메소드는 이미 실행됨
        Completion<T> completion = new Completion();
        future.addCallback(result -> {
            completion.complete(result);
        }, e -> completion.error(e));
        return completion;
    }

    public void complete(T result) {
        // 첫번째 메소드 실행 끝남
        if(next != null) next.run(result);
    }

    public void run(T result) {
        if(isAccept()) {
            consumer.accept(result);
        }
        else if (isApply()) {
            ListenableFuture<T> listenableFuture = function.apply(result);
            listenableFuture.addCallback(result1 -> complete(result1), e -> error(e));
        }
    }

    private boolean isApply() {
        return function != null;
    }

    private boolean isAccept() {
        return consumer != null;
    }

    public void error(Throwable e) {
        log.error(e.getMessage());
    }


    public Completion<T> andApply(Function<T, T> function) {
        Completion<T> completion = new Completion(function);
        this.next = completion;
        return completion;
    }

    public void andAccept(Consumer<T> consumer) {
        Completion<T> completion = new Completion<>(consumer);
        this.next = completion;
    }
}
