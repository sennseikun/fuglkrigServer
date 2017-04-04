package com.fuglkrig.server;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.json.*;

import java.util.List;

/**
 * Created by thoma on 3/24/2017.
 */
public class Lobby {

    private List<Player> players = new ArrayList<>();
    private int max_player_count;
    private String password;
    private String name;
    private boolean isStarted = false;

    /**
     * Creats a lobby with the creator of the lobby as the only player in the list at the start.
     * @param name of the game
     * @param players list of players
     * @param max_player_count maximum amount of player allowed
     * @param password if there is a PW to the lobby
     */
    public Lobby(String name,List<Player> players, int max_player_count, String password){
        this.players = players;
        this.max_player_count = max_player_count;
        this.password = password;
        this.name = name;
    }

    public void removePlayers(){
        players.clear();
    }

    /**
     * @return the host of the lobby
     */
    public Player getHost(){
        return players.get(0);
    }

    /**
     * Remove the player in the lobby.
     * @param name
     */
    public void removeByPlayerName(String name) {

        int value = -1;

        System.out.println("List of players: " + players);
        System.out.println("Name of player to remove: " + name);

        for(int i = 0; i < players.size(); i++){

            if(players.get(i).toString().equals(name)){
                System.out.println("Finds the player in the removebyname method");
                value = i;
                break;
            }
        }
        if(value != -1){
            players.remove(value);
        }
    }

    /**
     * Sets the name of the lobby
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Gets the name of the lobby
     * @return the name of the lobby
     */
    public String getName(){
        return name;
    }

    /**
     * Check if the lobby is empty
     * @return false if there is any players in the lobby
     */
    public boolean isEmpty(){
        return players.isEmpty();
    }

    /**
     * Adds a player to the lobby
     * @param player
     */
    public void addPlayer(Player player){
        players.add(player);
    }

    /**
     * Removes a player from the lobby
     * @param player
     */
    public void removePlayer(Player player){
        players.remove(player);
    }

    /**
     * Checks if a player is in the lobby and returns a boolean
     * @param player
     * @return true if the player is in the lobby
     */
    public boolean containsPlayer(Player player){
        return players.contains(player);
    }

    /**
     * Gives the list of the players in the lobby
     * @return a list of players
     */
    public List<Player> getPlayers(){
        return players;
    }

    /**
     * Give the max amount of alowed players in the game
     * @return the int value of max alowed players
     */
    public int getMax_player_count(){
        return max_player_count;
    }

    /**
     * Give the amount of players in the lobby
     * @return the count on players in the lobby
     */
    public int getPlayerCount(){
        return players.size();
    }

    /**
     * Give the password
     * @return the sting of the password
     */
    public String getPassword(){
        return password;
    }

    /**
     * Create a json of the lobby
     * @return a json of the lobby
     */
    public JSONObject makeData() {
        JSONObject lobbyData = new JSONObject();

        lobbyData.put("maxPlayers", max_player_count);
        JSONArray playerList = new JSONArray(players);
        lobbyData.put("players", playerList);

        //returns the lobby
        return lobbyData;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }
}
