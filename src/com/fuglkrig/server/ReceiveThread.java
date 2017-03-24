package com.fuglkrig.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
            OnlinePlayers onlinePlayers = new OnlinePlayers();
            try {
                System.out.println("ID"+id);

                DataInputStream in;
                in = new DataInputStream(inputSocket.getInputStream());
                String message = in.readUTF();
                Player player;

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
                        ArrayList<Player> op = onlinePlayers.getPlayers();
                        for (int i = 0; op.size() < i ; i++) {
                            if (!op.get(i).equals(name)){
                                player = new Player(name, id, 0, inputSocket);
                                onlinePlayers.newPlayer(player);
                            }
                            else{
                                JSONObject retur = new JSONObject();
                                try {
                                    retur.put("userValid", "0");
                                    retur.put("pId", json.getString("playerID"));
                                    retur.put("nick", name);
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                DataOutputStream out = new DataOutputStream(inputSocket.getOutputStream());
                                out.writeUTF(retur.toString());
                            }
                        }

                    }catch(JSONException e){
                        e.printStackTrace();
                        break;
                    }
                }
                else if(datatype == 1){

                }
                else if(datatype == 2){

                }
                else{
                    break;
                }

                System.out.println(message);
                System.out.println("Hello world");


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stopConnection();
        stopThread();
    }
}
