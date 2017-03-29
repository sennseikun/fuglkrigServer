package com.fuglkrig.server;


import java.util.*;

import org.json.*;

import static java.util.Random.*;

/**
 * Created by Magnus on 03.03.2017.
 */


public class Game extends Thread{
    List<Player> players;
    Map currentMap;
    Thread game;
    Date lastTimeUpdatePlayers;
    double timeStart = System.currentTimeMillis();
    double timeSinceLastPowerUp;
    double timeForNewPowerUp = 500;
    String textOnPlayerScreen;
    int secoundsUntilStart = 6;
    int numberOfPowerUps = 8;
    Random rand = new Random();
    Powerup powerup;

    //powerups on map
    ArrayList<Powerup> powerupsOnMap;


    //this is a count down used to(Start?)
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

    public Game(List<Player> players) {
        this.players = players;
        this.sleepTime = 1000 / 30;
        this.paused = true;
        this.game = new Thread(this);
        this.textOnPlayerScreen = "";
    }

    //updates all clients in the game
    public void UpdateGame() {
        //builds the data that is going to be pushed

        //initial json document
        JSONObject dataToPlayers = new JSONObject();

        //liste over powerups på kart


        //list over players
        JSONArray playersData = new JSONArray();
        for (Player player : players) {
            //playerdocument
            JSONObject playerData = new JSONObject();
            playerData.put("playerID", player.getPlayerID());
            playerData.put("Name", player.getNick());
            playerData.put("Hp", player.getHp());
            playerData.put("Skin", player.getSkin());
            playerData.put("coordX", player.getCoordX());
            playerData.put("coordY", player.getCoordY());
            playerData.put("Direction", player.getDirection());
            playerData.put("Speed", player.getSpeed());
            playerData.put("Alive", player.getAlive());
            playerData.put("powerups", player.getPowerups());

            //put playerdocument to list over players.
            playersData.put(playerData);
        }
        //puts all players in the initial json document
        dataToPlayers.put("players", playersData);

        //pushes the data to the clients.
        for (Player player : players) {
            player.UpdateClient();
        }
    }

    public void SpawnPowerups() {
        //Used to spawn powerups
        timeSinceLastPowerUp = System.currentTimeMillis()-timeStart;
        if(timeSinceLastPowerUp > timeForNewPowerUp){
            powerup.setId(rand.nextInt(numberOfPowerUps)+1);
            this.powerupsOnMap.add(powerup);
            timeStart = System.currentTimeMillis();
        }
    }

    public void MovePowerups() {
        for (Powerup powerup : powerupsOnMap) {
            if (powerup.getX() < 0 ) {
                powerupsOnMap.remove(powerup);
            }
            else {
                powerup.tick();
            }
        }
    }

    //sleeps a game for tick time. Used to avoid a billion try/catch in the code.
    public void sleepTick() {
        try {
            game.sleep(sleepTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void playerTick() {
        for (Player player : players) {
            player.nextTick();
        }
    }

    //logic for the game thread. Do not run this method. run the start() even tho it is not specified here
    @Override
    public void run() {
        System.out.println("gameloop started");
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
            sleepTick();
    	}

        //counts down the game before its starts
        timer.schedule(countDown, 1000,1000);
        paused = false;

    	//start updating players
    	while(!paused) {
            System.out.println("running tick");
            SpawnPowerups();
            playerTick();
    		UpdateGame();
    		sleepTick();
    	}
    }

}
