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

    public Lobby(String name,List<Player> players, int max_player_count, String password){
        this.players = players;
        this.max_player_count = max_player_count;
        this.password = password;
        this.name = name;
    }

    public Player getHost(){
        return players.get(0);
    }

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

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public boolean isEmpty(){
        return players.isEmpty();
    }

    public void addPlayer(Player player){
        players.add(player);
    }
    public void removePlayer(Player player){
        players.remove(player);
    }
    public boolean containsPlayer(Player player){
        return players.contains(player);
    }
    public List<Player> getPlayers(){
        return players;
    }

    public int getMax_player_count(){
        return max_player_count;
    }

    public int getPlayerCount(){
        return players.size();
    }
    public String getPassword(){
        return password;
    }

    //make json of lobby
    public JSONObject makeData() {
        JSONObject lobbyData = new JSONObject();

        lobbyData.put("maxPlayers", max_player_count);
        JSONArray playerList = new JSONArray(players);
        lobbyData.put("players", playerList);

        //returns the lobby
        return lobbyData;
    }

}
