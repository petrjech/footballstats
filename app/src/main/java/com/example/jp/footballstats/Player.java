package com.example.jp.footballstats;

class Player {

    private long id;
    private String name;

    Player(long id, String name) {
        this.id     = id;
        this.name = name;
    }

    long getId() {
        return id;
    }

    String getName() {
        return name;
    }
}