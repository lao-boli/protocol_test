package org.hqu.lly.struct;

import lombok.Getter;

@Getter
public class Pair<K, V> {

    private final K first;
    private final V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;

    }


}
