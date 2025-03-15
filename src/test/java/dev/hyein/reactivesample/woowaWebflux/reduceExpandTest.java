package dev.hyein.reactivesample.woowaWebflux;

import org.junit.jupiter.api.Test;

class reduceExpandTest {

    reduceExpand reduceExpand = new reduceExpand();

    @Test
    void expand() {
        reduceExpand.expand();
    }

    @Test
    void reduce() {
//        monoPractice.reduceLegacy();
        reduceExpand.reduce();
    }
}
