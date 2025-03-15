package dev.hyein.reactivesample.woowaWebflux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.function.Function3;
import reactor.util.function.Tuple2;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public class NestedFlatMap {

    public Mono<Integer> nestedFlatMap() {
        AtomicReference<String> prefix = new AtomicReference<>("Prefix");

        return _1번_작업()
            .flatMap(firstResult -> {
                if (firstResult == 0) {
                    return Mono.error(new RuntimeException("result is null"));
                }
                prefix.set(String.valueOf(firstResult));
                return _2번_작업(firstResult)
                    .flatMap(secondResult -> {
                            if (secondResult == 0) {
                                return Mono.error(new RuntimeException("result is null"));
                            }
                            return _3번_작업(secondResult)
                                .map(result2 -> firstResult + secondResult + result2);
                        }
                    );
            });
    }

    /**
     * 된다!
     * 번외_tuple_리팩토링2 - 타입 생략
     *
     * @return
     */
    public Mono<Integer> nestFlatMapRefactoring() {
        AtomicReference<String> prefix = new AtomicReference<>("Prefix");

        return _1번_작업()
            .filter(firstResult -> firstResult != 0)
            .doOnNext(firstResult -> prefix.set(String.valueOf(firstResult)))
            .zipWhen(firstResult -> _2번_작업(firstResult))
            .filter(result -> function((_ignore, secondResult) -> secondResult != 0).apply(result))
            .zipWhen(result -> function((_ignore, secondResult) -> _3번_작업(secondResult)).apply(result),
                (result, result2) ->
                    function((firstResult, secondResult, thirdResult) -> firstResult + secondResult + thirdResult)
                        .apply(result, result2))
            .switchIfEmpty(Mono.error(new RuntimeException("result is null")));
    }

    public static <R> Function<Tuple2<Integer, Integer>, R> function(BiFunction<Integer, Integer, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2());
    }

    public static <R> BiFunction<Tuple2<Integer, Integer>, Integer, R> function(Function3<Integer, Integer, Integer, R> function) {
        return (tuple, v) -> function.apply(tuple.getT1(), tuple.getT2(), v);
    }


    /**
     * 된다!
     *
     * @return
     */
    public Mono<Integer> 번외_tuple_리팩토링() {
        AtomicReference<String> prefix = new AtomicReference<>("Prefix");

        return _1번_작업()
            .filter(firstResult -> firstResult != 0)
            .doOnNext(firstResult -> prefix.set(String.valueOf(firstResult)))
            .zipWhen(firstResult -> _2번_작업(firstResult))
            .filter(result -> c_function((Integer firstResult, Integer secondResult) -> secondResult != 0).apply(result))
            .zipWhen(result -> _3번_작업(result.getT2()),
                (result, result2) ->
                    c_function((Integer firstResult, Integer secondResult, Integer thirdResult) -> {
                            log.info("return complete: {}, {}, {}", firstResult, secondResult, thirdResult);
                            return firstResult + secondResult + thirdResult;
                        }
                    ).apply(result, result2))
            .switchIfEmpty(Mono.error(new RuntimeException("result is null")));
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> c_function(BiFunction<T1, T2, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2());
    }

    public static <T1, T2, T3, R> BiFunction<Tuple2<T1, T2>, T3, R> c_function(Function3<T1, T2, T3, R> function) {
        return (tuple, v) -> function.apply(tuple.getT1(), tuple.getT2(), v);
    }


    /**
     * 된다!
     *
     * @return
     */
    public Mono<Integer> zipwhen_good() {
        AtomicReference<String> prefix = new AtomicReference<>("Prefix");

        return _1번_작업()
            .filter(firstResult -> firstResult != 0)
            .doOnNext(firstResult -> prefix.set(String.valueOf(firstResult)))
            .zipWhen(firstResult -> _2번_작업(firstResult))
            .filter(result -> result.getT2() != 0)
            .zipWhen(result -> _3번_작업(result.getT2()),
                (result, result2) -> result.getT1() + result.getT2() + result2
            )
            .switchIfEmpty(Mono.error(new RuntimeException("result is null")));
    }

    /**
     * zipWhen: flatMap 은 Mono.just 해줘야하는데 얜 안해줘도 되는 편안함
     *
     * @return
     */
    public Mono<Integer> zipWhen_map() {
        AtomicReference<String> prefix = new AtomicReference<>("Prefix");

        return _1번_작업()
            .filter(firstResult -> firstResult != 0)
            .doOnNext(firstResult -> prefix.set(String.valueOf(firstResult)))
            .zipWhen(firstResult -> _2번_작업(firstResult))
            .filter(result -> result.getT2() != 0)
            .zipWhen(result -> _3번_작업(result.getT2()))
            .map((result) -> {
                Integer firstResult = result.getT1().getT1();
                Integer secondResult = result.getT1().getT2();
                Integer thirdResult = result.getT2();
                return firstResult + secondResult + thirdResult;
            })
            .switchIfEmpty(Mono.error(new RuntimeException("result is null")));
    }

    /**
     * @return
     */
    public Mono<Integer> flatmap_doOnNext() {
        AtomicReference<String> prefix = new AtomicReference<>("Prefix");

        return _1번_작업()
            .filter(firstResult -> firstResult != 0)
            .doOnNext(firstResult -> prefix.set(String.valueOf(firstResult)))
            .flatMap(firstResult -> Mono.zip(Mono.just(firstResult), _2번_작업(firstResult)))
            .filter(result -> result.getT2() != 0)
            .flatMap(result -> Mono.zip(Mono.just(result), _3번_작업(result.getT2())))
            .map(result2 -> {
                Integer first = result2.getT1().getT1();
                Integer second = result2.getT1().getT2();
                Integer third = result2.getT2();
                return first + second + third;
            })
            .switchIfEmpty(Mono.error(new RuntimeException("result is null")));
    }


    /**
     * doOnEach 는 값 방출 뿐만 받는게 아니라서 onComplete 시그널을 받으면 NPE 난다.
     * 해결: doOnNext()
     * _3번_작업() 에서 firstResult 를 들고 올 수 없다
     *
     * @return
     */
    public Mono<Integer> flatMap_doOnEach() {
        AtomicReference<String> prefix = new AtomicReference<>("Prefix");

        return _1번_작업()
            .doOnEach(signal -> {
                if (signal.get() == 0) {   //  NPE 발생!!
                    throw new RuntimeException("firstResult is null");
                }
                prefix.set(String.valueOf(signal.get()));
            })
            .flatMap(firstResult -> Mono.zip(Mono.just(firstResult), _2번_작업(firstResult)))
            .doOnEach(signal -> {
                if (signal.get().getT2() == 0) {
                    throw new RuntimeException("result is null");
                }
            })
            .flatMap(result -> Mono.zip(Mono.just(result), _3번_작업(result.getT2())))
            .map(result2 -> {
                Integer first = result2.getT1().getT1();
                Integer second = result2.getT1().getT2();
                Integer third = result2.getT2();
                return first + second + third;
            });
    }

    private Mono<Integer> _1번_작업() {
        Integer value;
        value = 1;
//        value = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("[IN METHOD] _1번_작업 called: {}", value);
        return Mono.just(value);
    }

    private Mono<Integer> _2번_작업(int firstResult) {
        Integer value = firstResult * 10;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("[IN METHOD] _2번_작업 called: {}", value);
        return Mono.just(value);
    }

    private Mono<Integer> _3번_작업(int secondResult) {
        Integer value = secondResult * 10;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("[IN METHOD] _3번_작업 called: {}", value);
        return Mono.just(value);
    }

}
