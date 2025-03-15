package dev.hyein.reactivesample.woowaWebflux;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class Node {

    private Integer id;
    @Setter
    private List<Node> nodes;
}
