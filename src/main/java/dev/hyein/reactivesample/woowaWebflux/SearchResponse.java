package dev.hyein.reactivesample.woowaWebflux;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchResponse {

    private int totalCount;
    private int page;
    private int resultSize;

}
