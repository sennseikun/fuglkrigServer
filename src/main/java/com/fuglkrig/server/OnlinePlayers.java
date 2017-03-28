package main.java.com.fuglkrig.server;

import java.util.ArrayList;
import java.util.List;

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

    public static boolean hasPlayer(String nick){
        for(Player p: players){
            if(p.getNick().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public static Player getPlayer(String name){
        for(Player p: players){
            if(p.getNick().equals(name)){
                return p;
            }
        }
        return null;
    }

    public static void removePlayer(Player player){
        if(players.contains(player)){
            players.remove(player);
        }
    }
    public static void removeAllPlayers(List<Player> list){
        players.removeAll(list);
    }
}
