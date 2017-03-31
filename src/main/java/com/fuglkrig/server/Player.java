package com.fuglkrig.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus on 03.03.2017.
 */
public class Player {
    String nick;
    int playerID;
    int hp;
    int skin;
    int targetPosX;
    int targetPosY;
    int coordX;
    int coordY;
    int xSpeed;
    int ySpeed;
    int direction;
    int speed;
    boolean alive;

    /**
     * to be able do to communication we need the socket here
     */
    Socket playerSocket;

    ArrayList<Integer> powerups;
    Game currentGame;

    /**
     * Changes the toString to return the name of the player.
     * @return the name of the player
     */
    @Override
    public String toString(){
        return nick;
    }

    /**
     * Create a player with these param
     * @param nick
     * @param playerID
     * @param skin
     * @param connection
     */
    public Player(String nick, int playerID, int skin, Socket connection) {
        this.nick = nick;
        this.playerID = playerID;
        this.skin = skin;
        this.playerSocket = connection;
        this.alive = true;
        this.targetPosX = 1;
        this.targetPosY = 1;
        this.coordX = 5;
        this.coordY = 5;
        this.xSpeed = 5;
        this.ySpeed = 5;

    }

    /**
     * Checks if the nick of 2 players equals each other
     * @param o
     * @return true if the nicks are equal
     */
    @Override
    public boolean equals(Object o){

        Player p = (Player)o;

        return getNick().equals(p.getNick());
    }

    /**
     * Adds a player to the game lobby
     * @param lobbyID
     * @param name
     * @param players
     */
    public void addToGameLobby(String lobbyID,String name, List<Player> players){

        try{
            JSONObject sendJson = new JSONObject();
            sendJson.put("hostPlayer",LobbyList.getLobby(lobbyID).getHost().getNick());

            if(name.equals(getNick())){

                sendJson.put("Datatype","4");
                sendJson.put("LobbyID",lobbyID);
                sendJson.put("Error","0");
                sendJson.put("PlayerName",name);
                sendJson.put("PlayerCount",Integer.toString(players.size()));

                for(int i = 0; i < players.size(); i++){
                    System.out.println(sendJson);
                    System.out.println(players.get(i));
                    sendJson.put("PlayerName" + i, players.get(i).getNick());
                    System.out.println(sendJson);
                }
            }

            else{
                sendJson.put("Datatype","4");
                sendJson.put("LobbyID",lobbyID);
                sendJson.put("Error","0");
                sendJson.put("PlayerName",name);
                sendJson.put("PlayerCount",Integer.toString(players.size()));
            }

            System.out.println(playerSocket);
            System.out.println(sendJson);

            DataOutputStream out = new DataOutputStream(playerSocket.getOutputStream());
            out.writeUTF(sendJson.toString());

            System.out.println("Sent adding update to player: "+getNick());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Remove the player from the given lobby
     * @param name
     * @param lobbyID
     * @param players
     */
    public void removeFromGameLobby(String name, String lobbyID,List<Player> players){
        try{
            JSONObject sendJson = new JSONObject();

            sendJson.put("Datatype","3");
            sendJson.put("LobbyID",lobbyID);
            sendJson.put("Error","0");
            if(LobbyList.getLobby(lobbyID).getPlayers().size() > 1){
                sendJson.put("hostPlayer",LobbyList.getLobby(lobbyID).getPlayers().get(1).getNick());
            }
            else{
                sendJson.put("hostPlayer",LobbyList.getLobby(lobbyID).getPlayers().get(0).getNick());
            }

            sendJson.put("PlayerCount",Integer.toString(players.size()-1));

            sendJson.put("PlayerName",name);

            DataOutputStream out = new DataOutputStream(playerSocket.getOutputStream());
            out.writeUTF(sendJson.toString());

            System.out.println("Sent removing update to player: "+getNick());
        }catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * @return the players socket
     */
    public Socket getPlayerSocket(){
        return playerSocket;
    }

    /**
     * @return the players nick
     */
    public String getNick() {
        return this.nick;
    }

    /**
     * @return the playerID
     */
    public int getPlayerID() {
        return this.playerID;
    }

    /**
     * @return the players HP
     */
    public int getHp() {
        return this.hp;
    }

    /**
     * @return the players skin
     */
    public int getSkin() {
        return this.skin;
    }

    /**
     * @return the players target x position
     */
    public int getTargetPosX() {
        return this.targetPosX;
    }

    /**
     * @return the players target y position
     */
    public int getTargetPosY() {
        return this.targetPosY;
    }

    /**
     * @return the players current x position
     */
    public int getCoordX() {
        return this.coordX;
    }

    /**
     * @return the players current y position
     */
    public void setCoordX(int x) {
        this.coordX = x;
    }

    public void setCoordY(int y) {
        this.coordY = y;
    }

    public void setTargetPosX (int x) {
        this.targetPosX = x;
    }

    public void setTargetPosY (int y) {
        this.targetPosY = y;
    }


    public int getCoordY() {
        return this.coordY;
    }

    /**
     * @return the players direction
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * @return the players current speed
     */
    public int getSpeed() {
        return this.speed;
    }

    /**
     * @return if the player is alive or not
     */
    public boolean getAlive() {
        return this.alive;
    }

    /**
     * @return a json of the players powerup
     */
    public JSONArray getPowerups() {
        JSONArray powerups = new JSONArray(this.powerups);
        return powerups;
    }

    /**
     * Adding powerup to the player
     * @param powerUp
     */
    public void addPowerUp(int powerUp){
        this.powerups.add(powerUp);
    }

    /**
     * Setting the alive state of a player.
     * @param alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Setting a game for the player
     * @param game
     */
    public void setGame(Game game) {
        this.currentGame = game;
    }

    /**
     * todo fix this comment!
     * Update function for the changing of direction of the player.
     */
    public void nextTick() {

        System.out.println("old pos: " + coordX + " " + coordY);
        System.out.println("target pos: " + targetPosX + " " + targetPosY);
        int dx = coordX - targetPosX;
        int dy = coordY - targetPosY;
        int targetPosLength = (int) Math.sqrt(dx*dx + dy*dy);

        this.xSpeed = dx/targetPosLength;
        this.ySpeed = dy/targetPosLength;


        if ((dx > 0 && coordX >= targetPosX) || (dx < 0 && coordX <= targetPosX)){
            this.xSpeed = 0;
        }
        if ((dy > 0 && coordY >= targetPosY) || (dy < 0 && coordY <= targetPosY)){
            this.ySpeed = 0;
        }

        this.coordX += xSpeed * 15;
        this.coordY += ySpeed * 15;
        System.out.println("new pos: " + coordX + " " + coordY);
    }


    /**
     * make all game specific variables something that doesnt make sence. like negatives.
     */
    public void notInGame() {
        this.hp = -1;
        this.targetPosY = -1;
        this.targetPosX = -1;
        this.coordX = -1;
        this.coordY = -1;
        this.direction = -1;
        this.speed = -1;
        this.alive = false;
        this.powerups.clear();
        this.currentGame = null;
    }

    /**
     * send the data that comes from the game out to the client
     */
    public void UpdateClient(JSONObject data) {

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(playerSocket.getOutputStream());
            out.writeUTF(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updating the settings for this player.
     * @param skin
     * @param nick
     */
    public void UpdatePlayerSettings(int skin, String nick) {
        this.nick = nick;
        this.skin = skin;
    }

    /**
     * Checks if the player is ready to start
     * @return false until the player is ready to start
     */
    public boolean readyToStart() {
    	return true;
    }

}
