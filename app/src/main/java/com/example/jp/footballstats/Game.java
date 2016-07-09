package com.example.jp.footballstats;

class Game {

    private long playerID;
    private long gameID;
    private String date;
    private int result;
    private int elo;
    private String note = "";

    Game () {}

    Game(long gameID) {
        this.gameID = gameID;
    }

    long getGameID() {
        return gameID;
    }

    void setDate(String date) {
        this.date = date;
    }

    void setResult(int result) {
        this.result = result;
    }

    void setElo(int elo) {
        this.elo = elo;
    }

    void setNote(String note) {
        this.note = note;
    }

    void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    long getPlayerID() {
        return playerID;
    }

    String getDate() {
        return date;
    }

    int getResult() {
        return result;
    }

    int getElo() {
        return elo;
    }

    String getNote() {
        return note;
    }
}
