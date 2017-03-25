package com.fuglkrig.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thoma on 3/23/2017.
 */
public class ReceiveThread extends Thread {

    Socket inputSocket;
    int id;
    Player player;
    boolean running;


    public ReceiveThread(Socket inputSocket, int id){
        this.inputSocket = inputSocket;
        this.id = id;
        this.running = true;
    }

    public void stopThread(){
        this.running = false;
    }

    public void stopConnection(){
        try {
            inputSocket.close();
            Lobby removeLobby = null;
            OnlinePlayers.removePlayer(player);
            for(Lobby l: LobbyList.getLobbys()){
                if(l.containsPlayer(player)){
                    l.removePlayer(player);
                    if(l.isEmpty()){
                        removeLobby = l;
                    }
                }
            }
            if(removeLobby != null){
                LobbyList.removeLobby(removeLobby);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*
        userValid = 0/1
        uId = "return userID"
        nick = "return nick"
     */


    @Override
    public void run(){
        while(running){
            try {

                System.out.println("Players: " + OnlinePlayers.getPlayers());


                DataInputStream in;
                in = new DataInputStream(inputSocket.getInputStream());
                String message = in.readUTF();

                System.out.println(message);

                int datatype = -1;

                try {
                    JSONObject json = new JSONObject(message);
                    System.out.println(json);
                    datatype = json.getInt("Datatype");
                }
                catch(JSONException e){
                    e.printStackTrace();
                    break;
                }

                //Check if username is taken, register this user on this username

                if(datatype == 0){
                    String name;
                    try{
                        JSONObject json = new JSONObject(message);
                        name = json.getString("nick");
                        String value = "1";
                        ArrayList<Player> op = OnlinePlayers.getPlayers();

                        //Checks if name is empty

                        if(name.equals("")){
                            value = "0";
                        }

                        //Checks if other users already has this username

                        else if(op != null){
                            for (int i = 0; op.size() < i ; i++) {
                                if (op.get(i).getNick().equals(name)) {
                                    value = "0";
                                }
                            }
                        }

                        //Create and send answer to send back to client

                        JSONObject retur = new JSONObject();
                        retur.put("Datatype","0");
                        retur.put("userValid", value);
                        retur.put("pId", id);
                        retur.put("nick", name);

                        if(value.equals("1")){

                            player = new Player(name,id,0,inputSocket);
                            OnlinePlayers.newPlayer(player);
                        }

                        DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                        out.writeUTF(retur.toString());

                        System.out.println("ReceiveThread: Sent user verification");

                    }catch(JSONException e){
                        e.printStackTrace();
                        break;
                    }
                }

                //Asking for list of lobbys

                else if(datatype == 1 || datatype == 10){

                    //Lobby logic here
                    JSONObject json = new JSONObject();
                    json.put("Datatype","1");

                    //Remove the lobbies without users in it

                    LobbyList.remove_empty_lists();

                    List<Lobby> lobbys = LobbyList.getLobbys();

                    json.put("Count",lobbys.size());

                    //Return all the lobbies with lobby name, player count, max player count and password

                    for(int i = 1; i < lobbys.size() + 1; i ++){

                        Lobby l = lobbys.get(i - 1);

                        json.put("Name"+i,l.getName());
                        json.put("PlayerCount"+i,l.getPlayerCount());
                        json.put("MaxPlayers"+i,l.getMax_player_count());
                        json.put("Password"+i,l.getPassword());
                    }

                    DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                    out.writeUTF(json.toString());

                    System.out.println("ReceiveThread: Sent lobbylist");


                }

                //Create lobby
                else if(datatype == 2){

                    //Setup a new lobby and put the creating player in it

                    JSONObject jsonObject = new JSONObject(message);
                    String name = jsonObject.getString("Name");
                    String playerName = jsonObject.getString("PlayerName");
                    int max_players = jsonObject.getInt("Players");
                    String password = jsonObject.getString("Password");

                    List<Player> playerList = new ArrayList<>();
                    Player player = new Player(playerName,id,0,inputSocket);

                    playerList.add(player);

                    Lobby lobby = new Lobby(name,playerList,max_players,password);

                    System.out.println("ReceiveThread: Creating a lobby and putting it in the list");
                    LobbyList.addLobby(lobby);

                    JSONObject sendJson = new JSONObject();

                    sendJson.put("Datatype","2");
                    sendJson.put("Valid","1");

                    DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                    out.writeUTF(sendJson.toString());


                }


                //Remove player from lobby

                else if(datatype == 3){
                    JSONObject jsonObject = new JSONObject(message);
                    String name = jsonObject.getString("Name");
                    String lobbyID = jsonObject.getString("Lobby");

                    List<Lobby> lobbys = LobbyList.getLobbys();
                    Lobby currLobby = null;

                    for(Lobby l: lobbys){
                        if(l.getName().equals(lobbyID)){
                            l.removeByPlayerName(name);
                            currLobby = l;
                            break;
                        }
                    }

                    String playerCount = "0";
                    JSONObject sendJson = new JSONObject();

                    if(currLobby != null){
                        playerCount = Integer.toString(currLobby.getPlayers().size());
                        System.out.println("ReceiveThread: Removed player: "+name);

                        System.out.println("ReceiveThread: Player count: "+playerCount);

                        sendJson.put("Datatype","3");
                        sendJson.put("LobbyID",lobbyID);
                        sendJson.put("Error","0");
                        sendJson.put("PlayerCount",playerCount);
                    }
                    else{
                        sendJson.put("Datatype","3");
                        sendJson.put("Error","1");
                    }


                    DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                    out.writeUTF(sendJson.toString());

                }

                //New player joining a game

                else if(datatype == 4){
                    JSONObject jsonObject = new JSONObject(message);
                    String name = jsonObject.getString("Name");
                    String lobbyID = jsonObject.getString("Lobby");

                    List<Lobby> lobbys = LobbyList.getLobbys();
                    Lobby currLobby = null;

                    //Add player to the lobby being joined

                    for(Lobby l: lobbys){
                        if(l.getName().equals(lobbyID)){

                            Player p = new Player(name,id,0,inputSocket);
                            l.addPlayer(p);
                            currLobby = l;
                            break;
                        }
                    }

                    String playerCount;

                    JSONObject sendJson = new JSONObject();

                    if(currLobby != null){
                        playerCount = Integer.toString(currLobby.getPlayers().size());
                        System.out.println("Added player: "+name);

                        sendJson.put("Datatype","4");
                        sendJson.put("LobbyID",lobbyID);
                        sendJson.put("Error","0");
                        sendJson.put("PlayerCount",playerCount);

                    }
                    else{
                        sendJson.put("Datatype","4");
                        sendJson.put("Error","1");
                    }

                    DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                    out.writeUTF(sendJson.toString());

                }

                else{
                    break;
                }

                System.out.println(message);


            } catch (IOException e) {
                e.printStackTrace();
                stopConnection();
                stopThread();
            }
        }

        stopConnection();
        stopThread();
    }
}
