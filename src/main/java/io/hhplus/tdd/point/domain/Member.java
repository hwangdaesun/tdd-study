package io.hhplus.tdd.point.domain;

import lombok.Getter;

@Getter
public class Member {

    private long id;

    public Member(long id) {
        this.id = id;
    }
}
