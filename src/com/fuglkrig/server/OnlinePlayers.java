package com.fuglkrig.server;

import java.util.ArrayList;

/**
 * Created by Tore on 24.03.2017.
 */
public class OnlinePlayers {
    private static ArrayList<Player>  players;

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(ArrayList<Player> p) {
        players = p;
    }

    public static void newPlayer(Player player){
        players.add(player);
    }
}
