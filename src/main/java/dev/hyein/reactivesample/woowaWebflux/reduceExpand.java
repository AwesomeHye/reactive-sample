package dev.hyein.reactivesample.woowaWebflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Mono
 * .just() v
 * .flatMap() v
 * .map() v
 * .flatMapMany() v
 * .index()
 * .flatMapSequential() v
 * .expand()
 * .reduce()
 * .zip() v
 * .fromCallable(supplier).subscribeOn(Schedulers.elastic())
 * .defer()
 * .error()
 * .doOnNext()
 * .create()
 * .doOnSuccess()
 * .doOnError()
 * .transform()
 */
@Slf4j
public class reduceExpand {

    public void expand() {
        Mono<Node> root = getHierarchicalNode();

        root
            .expand(node -> {
                log.info("expand called: {}", node.getId());
                if (CollectionUtils.isEmpty(node.getNodes())) {
                    return Mono.empty();
                }
                return Flux.fromIterable(node.getNodes());
            })
            .subscribe(o -> System.out.println(o.getId()));
    }

    private Mono<Node> getHierarchicalNode() {
        Node child1_1 = new Node(1000, null);
        Node child1_2 = new Node(2000, null);

        Node child2_1 = new Node(3000, null);
        Node child2_2 = new Node(4000, null);

        Node child1 = new Node(100, List.of(child1_1, child1_2));
        Node child2 = new Node(200, List.of(child2_1, child2_2));

        Node root = new Node(1, List.of(child1, child2));
        return Mono.just(root);
    }


    public void reduce() {
        // 결과가 100개 넘으면 expand 로 끝날때까지 실행한다.
        // reduce 로 count, data 를 한데 모은다.

        search(0)
            .expand(searchResponse -> {
                log.info("expand: {}", searchResponse.getPage());
                return searchNextNumber(searchResponse);
            })
            .map(searchResponse -> {
                log.info("map: {}", searchResponse.getPage());
                return searchResponse.getResultSize();
            })
            .reduce((resultSize1, resultSize2) -> {
                log.info("reduce: {} {}", resultSize1, resultSize2);
                return resultSize1 + resultSize2;
            })
            .subscribe(searchCount -> log.info("result: {}", searchCount))
        ;

    }

    /**
     * 페이지 당 검색 결과 반환
     *
     * @param page
     * @return
     */
    private static Mono<SearchResponse> search(int page) {
        int resultSize = getSearchResultSize(page);
        return Mono.just(new SearchResponse(23, page, resultSize));
    }

    /**
     * 검색 결과 수가 0이 아니면 다음 페이지로 검색
     *
     * @param searchResponse
     * @return
     */
    private static Mono<SearchResponse> searchNextNumber(SearchResponse searchResponse) {
        if (searchResponse.getResultSize() != 0) {
            return search(searchResponse.getPage() + 1);
        }
        return Mono.empty();
    }

    /**
     * page 의 검색 결과 수
     *
     * @param page
     * @return
     */
    private static Integer getSearchResultSize(Integer page) {
        switch (page) {
            case 0:
            case 1:
                return 10;
            case 2:
                return 3;
            default:
                return 0;
        }
    }

}
