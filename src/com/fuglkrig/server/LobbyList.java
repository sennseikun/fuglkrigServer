package com.fuglkrig.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Magnus on 03.03.2017.
 */
public class LobbyList {

    private static List<Lobby> lobbys = new ArrayList<>();

    public static void addLobby(Lobby lobby){
        lobbys.add(lobby);
    }

    public static void removeLobby(Lobby lobby){
        lobbys.remove(lobby);
    }

    public static  List<Lobby> getLobbys(){
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
