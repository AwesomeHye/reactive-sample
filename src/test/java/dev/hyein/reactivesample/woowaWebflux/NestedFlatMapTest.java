package dev.hyein.reactivesample.woowaWebflux;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class NestedFlatMapTest {

    NestedFlatMap nestedFlatMap = new NestedFlatMap();

    @Test
    void nestedFlatMap() {
        nestedFlatMap.nestedFlatMap().subscribe(
            result -> {
                assertEquals(6, result);
            },
            error -> {
                fail();
            }
        );
    }

    @Test
    void nestedFlatMapRefactoring() {
        nestedFlatMap.nestFlatMapRefactoring().subscribe(
            result -> {
                assertEquals(111, result);
            }
        );
    }

}
