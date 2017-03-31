package com.fuglkrig.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tore on 24.03.2017.
 */

public class OnlinePlayers {
    /**
     * Creates a arraylist for the players
     */
    private static ArrayList<Player>  players = new ArrayList<>();

    /**
     * @return the list of players
     */
    public static ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the list of players(never used)
     * @param p
     */
    public static void setPlayers(ArrayList<Player> p) {
        players = p;
    }

    /**
     * Puts a new player in to the list of online players
     * @param player
     */
    public static void newPlayer(Player player){
        players.add(player);
        System.out.println("Added player");
    }

    /**
     * Check if a player in online
     * @param nick of the player
     * @return true if the player is online
     */
    public static boolean hasPlayer(String nick){
        for(Player p: players){
            if(p.getNick().equals(nick)){
                return true;
            }
        }
        return false;
    }

    /**
     * Return the given player
     * @param name of the player
     * @return the player with the given name
     */
    public static Player getPlayer(String name){
        for(Player p: players){
            if(p.getNick().equals(name)){
                return p;
            }
        }
        return null;
    }

    /**
     * Removes the given player
     * @param player
     */
    public static void removePlayer(Player player){
        if(players.contains(player)){
            players.remove(player);
        }
    }

    /**
     * Remove all the players in the given list
     * @param list of players
     */
    public static void removeThesePlayers(List<Player> list){
        players.removeAll(list);
    }
}
