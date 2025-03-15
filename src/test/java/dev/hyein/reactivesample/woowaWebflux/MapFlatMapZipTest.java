package dev.hyein.reactivesample.woowaWebflux;

import org.junit.jupiter.api.Test;

class MapFlatMapZipTest {

    MapFlatMapZip mapFlatMapZip = new MapFlatMapZip();

    @Test
    void test() {
      /*  mapFlatMap.zip1();
        mapFlatMap.zip2();
        mapFlatMap.mono1();
        mapFlatMap.mono2();
        mapFlatMap.mix1();
        mapFlatMap.mix2();
        mapFlatMap.flatMapMany();*/
        mapFlatMapZip.flatMapSequential();
    }

}
