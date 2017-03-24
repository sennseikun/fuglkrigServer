package com.fuglkrig.server;

import java.util.ArrayList;

/**
 * Created by Magnus on 03.03.2017.
 */
public class Player {
    String name;
    int hp;
    ArrayList<Powerup> powerups;
    ReceiveThread receiveThread;
    Game currentGame;

    public Player(String name, ReceiveThread receiveThread) {
        this.name = name;
        this.receiveThread = receiveThread;
    }

    public void UpdateClient() {

    }

    public void UpdatePlayer() {

    }

    public boolean readyToStart() {
    	return true;
    }

}
