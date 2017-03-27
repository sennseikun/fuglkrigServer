package com.fuglkrig.server;

import org.json.JSONArray;

import java.net.Socket;
import java.util.ArrayList;

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
    int direction;
    int speed;
    boolean alive;

    //to be able do to communication we need the socket here
    Socket playerSocket;

    ArrayList<Integer> powerups;
    Game currentGame;

    @Override
    public String toString(){
        return nick;
    }

    public Player(String nick, int playerID, int skin, Socket connection) {
        this.nick = nick;
        this.playerID = playerID;
        this.skin = skin;
        this.playerSocket = connection;
    }

    public Socket getPlayerSocket(){
        return playerSocket;
    }

    public String getNick() {
        return this.nick;
    }

    public int getPlayerID() {
        return this.playerID;
    }

    public int getHp() {
        return this.hp;
    }

    public int getSkin() {
        return this.skin;
    }

    public int getTargetPosX() {
        return this.targetPosX;
    }

    public int getTargetPosY() {
        return this.targetPosY;
    }

    public int getCoordX() {
        return this.coordX;
    }

    public int getCoordY() {
        return this.coordY;
    }

    public int getDirection() {
        return this.direction;
    }

    public int getSpeed() {
        return this.speed;
    }

    public boolean getAlive() {
        return this.alive;
    }

    public JSONArray getPowerups() {
        JSONArray powerups = new JSONArray(this.powerups);
        return powerups;
    }

    public void addPowerUp(int powerUp){
        this.powerups.add(powerUp);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setGame(Game game) {
        this.currentGame = game;
    }

    public void setTargetPos(int x, int y) {
        //used to calculate the direction while setting the targetPos.
        //http://stackoverflow.com/questions/9970281/java-calculating-the-angle-between-two-points-in-degrees
        //no idea if it works, needs testing


        double tempAngle = Math.toDegrees(Math.atan2(y - coordY, x - coordX));
        this.direction = (int) tempAngle;

        if (this.direction < 0) {
            this.direction += 360;
        }

        this.targetPosX = x;
        this.targetPosY = y;

    }

    public void nextTick() {
        //http://stackoverflow.com/questions/1638437/given-an-angle-and-length-how-do-i-calculate-the-coordinates
        /*
        Aner ikke om dette faktisk funker.
         */
        this.coordX = (int) (this.coordX + speed * Math.cos((double) direction));
        this.coordY = (int) (this.coordY + speed * Math.sin((double) direction));
    }

    //make all game specific variables something that doesnt make sence. like negatives.
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

    //send the data that comes from the game out to the client
    public void UpdateClient() {

    }

    //update the data model of this player
    public void UpdatePlayer(int targetPosX, int targetPosY, int direction, int speed) {
        this.targetPosX = targetPosX;
        this.targetPosY = targetPosY;
        this.direction = direction;
        this.speed = speed;

    }

    //when the player updates his settings
    public void UpdatePlayerSettings(int skin, String nick) {
        this.nick = nick;
        this.skin = skin;
    }

    public boolean readyToStart() {
    	return true;
    }

}