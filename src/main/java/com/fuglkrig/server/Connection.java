package com.fuglkrig.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thoma on 3/23/2017.
 */
public class Connection extends Thread {

    private Socket inputSocket;
    private int id;
    private Player player;
    private boolean running;
    private ExecutorService executor;

    /**
     * Thread that is receiving threads
     * @param inputSocket
     * @param id
     */
    public Connection(Socket inputSocket, int id){
        this.inputSocket = inputSocket;
        this.id = id;
        this.setRunning(true);
        setExecutor(Executors.newFixedThreadPool(3));
    }

    /**
     * stopping the thread that is running
     */
    public void stopThread(){
        this.setRunning(false);
    }

    /**
     * stopping the connection with the client
     */
    public void stopConnection(){
        try {

            Lobby removeLobby = LobbyList.getLobbyWithPlayer(getPlayer());


            if(removeLobby != null){

                System.out.println("Found removelobby" + removeLobby.getName());

                for(Player p : removeLobby.getPlayers()){
                    if(!p.getNick().equals(getPlayer().getNick())){
                        System.out.println("Sending remove request to " + p.getNick());
                        p.removeFromGameLobby(getPlayer().getNick(),removeLobby.getName(),removeLobby.getPlayers());
                    }
                }
            }
            System.out.println("Removed player: " + getPlayer().getNick());
            //cleans up the game
            if (player.getCurrentGame() != null) {
                player.getCurrentGame().kickPlayer(player);
            }
            OnlinePlayers.removePlayer(getPlayer());
            LobbyList.updateLobbies();
            LobbyList.remove_empty_lists();

            getExecutor().shutdown();

            while (!getExecutor().isTerminated()) {
                //System.out.println("Terminating worker threads");
            }

            System.out.println("Terminated worker thread");

            getInputSocket().close();

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
        while(isRunning()){
            try {

                //TODO. her burde vi sjekke om det FAKTISK er data som venter. hvis ikke kan vi hoppe over resten. da slipper vi EOFException
                DataInputStream in;
                in = new DataInputStream(getInputSocket().getInputStream());
                String message = in.readUTF();

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
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"0", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                /**
                 * Asking for list of lobbys
                 */

                else if(datatype == 1 || datatype == 10){

                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"1", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                /**
                 * Create lobby
                 */
                else if(datatype == 2){
                    System.out.println("Executing worker thread 2");
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"2", getInputSocket(),message);
                    getExecutor().execute(worker);
                }


                /**
                 * Remove player from lobby
                 */

                else if(datatype == 3){
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"3", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                /**
                 * New player joining a game
                 */

                else if(datatype == 4){
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"4", getInputSocket(),message);
                    getExecutor().execute(worker);

                }

                else if(datatype == 5){
                    break;
                }

                else if(datatype == 8){
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"8", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                /**
                 * this starts the gameloop
                 */
                else if(datatype == 11) {
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"11", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                else if(datatype == 12) {
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"12", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                //client use powerup
                else if(datatype == 13) {
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"13", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                else if(datatype == 14){
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"14", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                else if(datatype == 25){
                    System.out.println("Gets into datatype 25");
                    Runnable worker = new WorkerThread(this, getReceiveThreadId(),"25", getInputSocket(),message);
                    getExecutor().execute(worker);
                }

                else{
                    break;
                }

            } catch (IOException e) {
                System.out.println("closed connection");
                /**
                 * kills the player so he isnt drawed on screen anymore.
                 */
                /*if (player.getCurrentGame() != null) {
                    player.setAlive(false);
                }*/
                break;
            }
        }

        stopConnection();
        stopThread();
    }

    public Socket getInputSocket() {
        return inputSocket;
    }

    public void setInputSocket(Socket inputSocket) {
        this.inputSocket = inputSocket;
    }

    public int getReceiveThreadId() {
        return this.id;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
}
