package com.fuglkrig.server;

/**
 * Created by Magnus on 03.03.2017.
 */
public class JoinGame {
    private Player player;
    private Game game;
    private String code;

    public void joinGame(String code) {

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
