package com.example.jp.footballstats;

class Game {

    //todo add formatted date
    private long     playerID;
    private String   date;
    private int      result;
    private int      elo;
    private String   note = "";

    Game (long playerID) {
        this.playerID = playerID;
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
