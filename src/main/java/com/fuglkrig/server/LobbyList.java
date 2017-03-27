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
