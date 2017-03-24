package com.fuglkrig.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Magnus on 03.03.2017.
 */


public class Game extends Thread{
    ArrayList<Player> players;
    Map currentMap;
    Thread game;
    Date lastTimeUpdatePlayers;
    String textOnPlayerScreen;
    int secoundsUntilStart = 6;


    //this is a count down used to
    Timer timer;
    TimerTask countDown = new TimerTask() {
        @Override
        public void run() {
            if (secoundsUntilStart == 0) {
                textOnPlayerScreen = "";
                countDown.cancel();
            }
            else {
                textOnPlayerScreen = Integer.toString(secoundsUntilStart - 1);
            }
            secoundsUntilStart--;
        }
    };

    //how many times a secound the game should update
    //1000 / tick = time between updates
    int sleepTime;

    //used to make sure everyone has loaded the game and is ready to start
    boolean paused;

    public Game(ArrayList<Player> players, Map map, int tick) {
        this.players = players;
        this.currentMap = map;
        this.sleepTime = 1000 / tick;
        this.paused = true;
        this.game = new Thread(this);
        this.textOnPlayerScreen = "";
    }

    //updates all clients in the game
    public void UpdateGame() {
        //builds the data that is going to be pushed

        //pushes the data to the clients.
        for (Player player : players) {
            player.UpdateClient();
        }
    }

    //sleeps a game for tick time. Used to avoid a billion try/catch in the code.
    public void runTick() {
        try {
            game.sleep(sleepTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //logic for the game thread
    public void run() {
        //Should probably make a timeout when we stop waiting for players to connect. todo

    	//Checks if all users are ready to start before moving on to other tasks.
        textOnPlayerScreen = "Waiting for players";
    	boolean readyToStart = false;
    	while (readyToStart == false) {
    		boolean everyoneReady = true;
            for (Player player : players) {
                if (player.readyToStart() == false) {
                	everyoneReady = false;
                }
            }
            //updates the players of the current state.
            UpdateGame();
            //reduces the amount of time this runs.
            runTick();
    	}

        //counts down the game before its starts
        timer.schedule(countDown, 1000,1000);
        paused = false;

    	//start updating players
    	while(!paused) {
    		UpdateGame();
    		runTick();
    	}
    }

    //starts the thread by invoking the run() method
    public void start() {
        game.start();
    }
}
