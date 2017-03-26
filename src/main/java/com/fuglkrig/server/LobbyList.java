package com.fuglkrig.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus on 03.03.2017.
 */
public class LobbyList {


    private static List<Lobby> lobbys = new ArrayList<>();

    public static void addLobby(Lobby lobby){
        lobbys.add(lobby);
    }

    public static List<Lobby> getLobbys(){
        return lobbys;
    }

    public void onChange(Game game) {

    }


}
