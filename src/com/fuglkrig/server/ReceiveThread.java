package com.fuglkrig.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    ExecutorService executor;


    public ReceiveThread(Socket inputSocket, int id){
        this.inputSocket = inputSocket;
        this.id = id;
        this.running = true;
        executor = Executors.newFixedThreadPool(5);
    }

    public void stopThread(){
        this.running = false;
    }

    public void stopConnection(){
        try {

            executor.shutdown();

            while (!executor.isTerminated()) {
                System.out.println("Terminating worker threads");
            }

            System.out.println("Terminated worker thread");

            inputSocket.close();
            Lobby removeLobby = null;
            OnlinePlayers.removePlayer(player);
            System.out.println("Removed player: " + player.getNick());
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
                System.out.println("Removed lobby from list");
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void setPlayer(Player player){
        this.player = player;
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

                DataInputStream in;
                in = new DataInputStream(inputSocket.getInputStream());
                String message = in.readUTF();

                System.out.println("Message received: " +message);

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
                    Runnable worker = new WorkerThread(this,id,"1",inputSocket,message);
                    System.out.println("Executing worker thread 1");
                    executor.execute(worker);
                }

                //Asking for list of lobbys

                else if(datatype == 1 || datatype == 10){

                    System.out.println("Executing worker thread 2");
                    Runnable worker = new WorkerThread(this,id,"2",inputSocket,message);
                    executor.execute(worker);
                }

                //Create lobby
                else if(datatype == 2){
                    System.out.println("Executing worker thread 3");
                    Runnable worker = new WorkerThread(this,id,"3",inputSocket,message);
                    executor.execute(worker);
                }


                //Remove player from lobby

                else if(datatype == 3){
                    System.out.println("Executing worker thread 4");
                    Runnable worker = new WorkerThread(this,id,"4",inputSocket,message);
                    executor.execute(worker);
                }

                //New player joining a game

                else if(datatype == 4){
                    System.out.println("Executing worker thread 5");
                    Runnable worker = new WorkerThread(this,id,"5",inputSocket,message);
                    executor.execute(worker);

                }

                else if(datatype == 5){

                    break;
                }
                else{
                    stopConnection();
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
                stopConnection();
                break;
            }
        }
        stopConnection();
        stopThread();
    }
}
