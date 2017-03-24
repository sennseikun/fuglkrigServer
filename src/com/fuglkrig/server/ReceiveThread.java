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
            OnlinePlayers.removePlayer(player);
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

                else if(datatype == 1){

                    //Lobby logic here
                    JSONObject json = new JSONObject();
                    json.put("Datatype","1");

                    List<Lobby> lobbys = LobbyList.getLobbys();

                    json.put("Count",lobbys.size());

                    for(int i = 1; i < lobbys.size() + 1; i ++){

                        Lobby l = lobbys.get(i - 1);

                        json.put("PlayerCount"+i,l.getPlayerCount());
                        json.put("MaxPlayers"+i,l.getMax_player_count());
                        json.put("Password"+i,l.getPassword());
                    }

                    DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                    out.writeUTF(json.toString());

                    System.out.println("Lobbyslist sent");


                }
                else if(datatype == 2){

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
