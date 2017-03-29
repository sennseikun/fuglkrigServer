package com.fuglkrig.server;

import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Magnus on 03.03.2017.
 */
public class LobbyList {

    private static List<Lobby> lobbys = new ArrayList<>();


    //Remove players no longer online from lobbies
    public static void updateLobbies(){

        for(Player p: OnlinePlayers.getPlayers()){
            System.out.println(("Online player:"+ p.getNick()));
        }


        for(Lobby l: lobbys){
            List<Player> removalPlayers = new ArrayList<>();
            for(Player p: l.getPlayers()){
                if(!OnlinePlayers.hasPlayer(p.getNick())){
                    System.out.println("Update lobbys: Adding player " +p.getNick() + " to removal players");
                    removalPlayers.add(p);
                }


            }

            l.getPlayers().removeAll(removalPlayers);
        }

    }

    public static Lobby getLobby(String name){
        for(Lobby l: lobbys){
            if(l.getName().equals(name)){
                return l;
            }
        }
        return null;
    }

    public static Lobby getLobbyWithPlayer(Player p){

        System.out.println("GetLobbyWithPlayer launched");

        for(Lobby l: lobbys){
            for(Player t1: l.getPlayers()){
                System.out.println("Player p "+p.getNick());
                System.out.println("Player t1 "+t1.getNick());
                if(t1.getNick().equals(p.getNick())){
                    return l;
                }
            }
        }
        System.out.println("Could not find player");
        return null;
    }

    public static List<Player> getPlayersFromLobby(Player player) {
        for (Lobby l: lobbys) {
            System.out.println("playernick from socket" + player.getNick());
            System.out.println("first nick from lobby" + l.getPlayers().get(0).getNick());
            if (l.getPlayers().get(0).equals(player)) {
                return l.getPlayers();
            }
        }
        return null;
    }

    public static void addLobby(Lobby lobby){
        lobbys.add(lobby);
    }

    public static void removeLobby(Lobby lobby){
        lobbys.remove(lobby);
    }

    public static List<Lobby> getLobbys(){
        return lobbys;
    }

    public void onChange(Game game) {

    }
    public static  void remove_empty_lists() {
        Iterator<Lobby> l =  lobbys.iterator();
        while(l.hasNext()){
            Lobby lobby = l.next();
            if(lobby.isEmpty()){
                l.remove();
            }
        }
    }


}
