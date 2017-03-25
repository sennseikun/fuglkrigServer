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

                if(datatype == 0){
                    String name;
                    try{
                        JSONObject json = new JSONObject(message);
                        name = json.getString("nick");
                        String value = "1";
                        ArrayList<Player> op = OnlinePlayers.getPlayers();

                        if(name.equals("")){
                            value = "0";
                        }

                        else if(op != null){
                            for (int i = 0; op.size() < i ; i++) {
                                if (op.get(i).getNick().equals(name)) {
                                    value = "0";
                                }
                            }
                        }


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

                        System.out.println("Player output sent");

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

                    int size = LobbyList.getLobbys().size();
                    List<Lobby> lobbys = LobbyList.getLobbys();

                    System.out.println(size+"");

                    for(int i = 0; i < size; i++){
                        if(lobbys.get(i).isEmpty()){

                            System.out.printf("Found empty lobby");
                            lobbys.remove(lobbys.get(i));
                        }
                    }


                    System.out.println("Number of lobbies: " + lobbys.size());

                    if(lobbys.size() == 0){
                        json.put("Count", 1);
                        json.put("Name1","Test");
                        json.put("PlayerCount1","1");
                        json.put("MaxPlayers1","3");
                        json.put("Password1","");
                    }
                    else{
                        json.put("Count",lobbys.size());
                    }

                    for(int i = 1; i < lobbys.size() + 1; i ++){

                        Lobby l = lobbys.get(i - 1);

                        json.put("Name"+i,l.getName());
                        json.put("PlayerCount"+i,l.getPlayerCount());
                        json.put("MaxPlayers"+i,l.getMax_player_count());
                        json.put("Password"+i,l.getPassword());
                    }

                    DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                    out.writeUTF(json.toString());

                    System.out.println("Lobbyslist sent");


                }

                //Create lobby
                else if(datatype == 2){
                    JSONObject jsonObject = new JSONObject(message);
                    String name = jsonObject.getString("Name");
                    int max_players = jsonObject.getInt("Players");
                    String password = jsonObject.getString("Password");

                    List<Player> playerList = new ArrayList<>();
                    Player player = new Player(name,id,0,inputSocket);

                    playerList.add(player);

                    Lobby lobby = new Lobby(name,playerList,max_players,password);

                    System.out.println("Adding lobby");
                    LobbyList.addLobby(lobby);

                    JSONObject sendJson = new JSONObject();

                    sendJson.put("Datatype","2");
                    sendJson.put("Valid","1");

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
