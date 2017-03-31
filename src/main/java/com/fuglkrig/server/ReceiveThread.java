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

    /**
     * Thread that is receiving threads
     * @param inputSocket
     * @param id
     */
    public ReceiveThread(Socket inputSocket, int id){
        this.inputSocket = inputSocket;
        this.id = id;
        this.running = true;
        executor = Executors.newFixedThreadPool(3);
    }

    /**
     * stopping the thread that is running
     */
    public void stopThread(){
        this.running = false;
    }

    /**
     * stopping the connection with the client
     */
    public void stopConnection(){
        try {

            System.out.println("Moves past here");

            Lobby removeLobby = LobbyList.getLobbyWithPlayer(player);


            if(removeLobby != null){

                System.out.println("Found removelobby" + removeLobby.getName());

                for(Player p : removeLobby.getPlayers()){
                    if(!p.getNick().equals(player.getNick())){
                        System.out.println("Sending remove request to " + p.getNick());
                        p.removeFromGameLobby(player.getNick(),removeLobby.getName(),removeLobby.getPlayers());
                    }
                }
            }

            OnlinePlayers.removePlayer(player);
            System.out.println("Removed player: " + player.getNick());
            LobbyList.updateLobbies();
            LobbyList.remove_empty_lists();

            executor.shutdown();

            while (!executor.isTerminated()) {
                //System.out.println("Terminating worker threads");
            }

            System.out.println("Terminated worker thread");

            inputSocket.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Connection the client to a player
     * @param player
     */
    public void setPlayer(Player player){
        this.player = player;
    }

    /*
        userValid = 0/1
        uId = "return userID"
        nick = "return nick"
     */

    /**
     * Function that runs the thread that's collection the connections
     */
    @Override
    public void run(){
        while(running){
            try {

                DataInputStream in;
                in = new DataInputStream(inputSocket.getInputStream());
                String message = in.readUTF();

                System.out.println("ReceiveThread: Message received: " +message);

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

                /**
                 * Check if username is taken, register this user on this username
                 */

                if(datatype == 0){
                    Runnable worker = new WorkerThread(this,id,"0",inputSocket,message);
                    System.out.println("Executing worker thread 0");
                    executor.execute(worker);
                }

                /**
                 * Asking for list of lobbys
                 */

                else if(datatype == 1 || datatype == 10){

                    System.out.println("Executing worker thread 1");
                    Runnable worker = new WorkerThread(this,id,"1",inputSocket,message);
                    executor.execute(worker);
                }

                /**
                 * Create lobby
                 */
                else if(datatype == 2){
                    System.out.println("Executing worker thread 2");
                    Runnable worker = new WorkerThread(this,id,"2",inputSocket,message);
                    executor.execute(worker);
                }


                /**
                 * Remove player from lobby
                 */

                else if(datatype == 3){
                    System.out.println("Executing worker thread 3");
                    Runnable worker = new WorkerThread(this,id,"3",inputSocket,message);
                    executor.execute(worker);
                }

                /**
                 * New player joining a game
                 */

                else if(datatype == 4){
                    System.out.println("Executing worker thread 4");
                    Runnable worker = new WorkerThread(this,id,"4",inputSocket,message);
                    executor.execute(worker);

                }

                else if(datatype == 5){
                    break;
                }

                else if(datatype == 8){
                    System.out.println("Executing worker thread 8");
                    Runnable worker = new WorkerThread(this,id,"8",inputSocket,message);
                    executor.execute(worker);
                }

                /**
                 * this starts the gameloop
                 */
                else if(datatype == 11) {
                    System.out.println("Executing worker thread 11");
                    Runnable worker = new WorkerThread(this,id,"11",inputSocket,message);
                    executor.execute(worker);
                }

                else{
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        stopConnection();
        stopThread();
    }
}
