package com.fuglkrig.server;

import java.util.ArrayList;

/**
 * Created by Tore on 24.03.2017.
 */

public class OnlinePlayers {
    private static ArrayList<Player>  players = new ArrayList<>();

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(ArrayList<Player> p) {
        players = p;
    }

    public static void newPlayer(Player player){
        players.add(player);
        System.out.println("Added player");
    }

    public static void removePlayer(Player player){
        if(players.contains(player)){
            players.remove(player);
        }
    }
}
