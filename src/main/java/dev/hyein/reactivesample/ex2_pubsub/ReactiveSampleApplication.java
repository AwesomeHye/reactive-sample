package dev.hyein.reactivesample.ex2_pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.reactivestreams.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@Slf4j
public class ReactiveSampleApplication {

    /**
     * 1~10 -> sum -> sub
     * @param args
     */
    public static void main(String[] args) {
        Publisher<Integer> publisher = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                Subscription subscription = new Subscription() {
                    Iterator<Integer> integers = Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList()).iterator();

                    @Override
                    public void request(long n) {
                        while (n-- > 0) {
                            if (integers.hasNext()) {
                                subscriber.onNext(integers.next());
                            }
                        }

                        subscriber.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                };
                subscriber.onSubscribe(subscription);
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            int sum = 0;
            @Override
            public void onSubscribe(Subscription s) {
                s.request(5);
            }

            @Override
            public void onNext(Integer integer) {
                sum += integer;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                log.info("complete: {}", sum);
            }
        };

        publisher.subscribe(subscriber);
    }






}
