package com.fuglkrig.server;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.json.*;

import javax.imageio.ImageIO;

import static java.util.Random.*;

/**
 * Created by Magnus on 03.03.2017.
 */


public class Game extends Thread {
    List<Player> players;
    Map currentMap;
    Thread game;
    Date lastTimeUpdatePlayers;
    double timeStart = System.currentTimeMillis();
    double timeSinceLastPowerUp;
    //in millisecounds
    double timeForNewPowerUp = 5000;
    String textOnPlayerScreen;
    int secoundsUntilStart = 6;
    int numberOfPowerUps = 8;
    Random rand = new Random();
    Powerup powerup;

    int gameSizeX = 1920;
    int gameSizeY = 1080;

    //powerups on map
    List<Powerup> powerupsOnMap;


    //this is a count down used to(Start?)
    Timer timer = new Timer();

    TimerTask countDown = new TimerTask() {
        @Override
        public void run() {
            if (secoundsUntilStart == 0) {
                textOnPlayerScreen = "";
                countDown.cancel();
            } else {
                textOnPlayerScreen = Integer.toString(secoundsUntilStart - 1);
            }
            secoundsUntilStart--;
        }
    };

    /**
     * how many times a secound the game should update
     * 1000 / tick = time between updates
     */
    int sleepTime;

    /**
     * used to make sure everyone has loaded the game and is ready to start
     */
    boolean paused;

    /**
     * used to check if there is a winner of the game
     */
    boolean lastManStanding;

    /**
     * Create a game with the list of players.
     *
     * @param players
     */
    public Game(List<Player> players) {
        this.players = players;
        this.sleepTime = 1000 / 30;
        this.lastManStanding = true;
        this.game = new Thread(this);
        this.textOnPlayerScreen = "";
        this.powerupsOnMap = new ArrayList();
    }

    /**
     * updates all clients in the game
     */
    public void UpdateGame() {
        /**
         * builds the data that is going to be pushed
         * initial json document
         * datatype 15
         */
        JSONObject dataToPlayers = new JSONObject();
        dataToPlayers.put("Datatype", 15);

        //liste over powerups på kart ??


        /**
         * list over players
         */
        JSONArray playersData = new JSONArray();
        for (Player player : players) {
            /**
             * playerdocument
             */
            JSONObject playerData = new JSONObject();
            playerData.put("PlayerID", player.getPlayerID());
            playerData.put("Name", player.getNick());
            playerData.put("Hp", player.getHp());
            playerData.put("Skin", player.getSkin());
            playerData.put("PosX", player.getCoordX());
            playerData.put("PosY", player.getCoordY());
            playerData.put("Direction", player.getDirection());
            playerData.put("Speed", player.getSpeed());
            playerData.put("Alive", player.getAlive());
            playerData.put("powerups", player.getPowerups());

            /**
             * put playerdocument to list over players.
             */
            playersData.put(playerData);
        }

        /**
         * puts all players in the initial json document
         */
        dataToPlayers.put("players", playersData);


        /**
         * pushes the data to the clients.
         */
        for (Player player : players) {
            player.UpdateClient(dataToPlayers);
        }
    }

    /**
     * Used to spawn powerups at a given time interval.
     * Adds a powerup to the powerupsOnMap list
     */
    public void SpawnPowerups() {
        //todo This needs to be fixed. needs to make a powerup before adding it to list currently we get nullpointer


        timeSinceLastPowerUp = System.currentTimeMillis() - timeStart;
        if (timeSinceLastPowerUp > timeForNewPowerUp) {

            int x, y, height, width, type;
            x = gameSizeX + 100;
            y = rand.nextInt(gameSizeY) + 1;
            BufferedImage img = null;

            //this needs to be the same as the number of powerups.
            type = rand.nextInt(8) + 1;
            try {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("powerup.png");
                img = ImageIO.read(is);
            } catch (IOException e) {
                System.out.println("Cant find powerup.bmp");
                System.out.println(e);
            }

            height = img.getHeight();
            width = img.getWidth();

            Powerup pu = new Powerup(x, y, height, width, type, img);
            pu.setType(rand.nextInt(numberOfPowerUps) + 1);
            this.powerupsOnMap.add(pu);
            timeStart = System.currentTimeMillis();
        }
    }

    public void MovePowerups() {
        List<Powerup> toDelete = new ArrayList<>();
        for (Powerup powerup : powerupsOnMap) {
            if (powerup.getX() < 0) {
                toDelete.add(powerup);
            } else {
                powerup.tick();
            }
        }

        if (toDelete.size() > 0) {

            for (Powerup powerup : toDelete) {
                System.out.println("removing powerup");
                powerupsOnMap.remove(powerup);
            }
        }
    }

    /**
     * sleeps a game for tick time. Used to avoid a billion try/catch in the code.
     */
    public void sleepTick() {
        try {
            game.sleep(sleepTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Is the update function of the players
     */
    public void playerTick() {
        for (Player player : players) {
            player.nextTick();
        }
    }

    /**
     * logic for the game thread. Do not run this method. run the start() even tho it is not specified here
     */
    public void startGame() {
        //this needs to send a package with datatype 14.
        JSONObject startGame = new JSONObject();
        startGame.put("Datatype", 14);
        startGame.put("Width", this.gameSizeX);
        startGame.put("Height", this.gameSizeY);

        JSONArray listOfPlayers = new JSONArray();

        for (Player player : players) {
            JSONObject playerData = new JSONObject();
            playerData.put("PlayerID", player.getPlayerID());

            listOfPlayers.put(playerData);
        }

        startGame.put("Players", listOfPlayers);

        System.out.println("\n\n\n\n" + startGame + "\n\n\n\n\n");

        //sends it to players
        for (Player player : players) {
            player.UpdateClient(startGame);
        }

    }

    public void lastManStanding() {
        int playersAlive = 0;
        for (Player player : players) {
            if (player.getAlive()) {
                playersAlive++;
            }
        }

        if (playersAlive <= 1) {
            lastManStanding = true;
        }
    }

    /**
     * logic for the game thread. Do not run this method. run the start() even tho it is not specified here
     */
    @Override
    public void run() {
        System.out.println("gameloop started");

        //todo Should probably make a timeout when we stop waiting for players to connect.

        /**
         * Checks if all users are ready to start before moving on to other tasks.
         */
        textOnPlayerScreen = "Waiting for players";
        boolean readyToStart = true;
        while (readyToStart == false) {

            boolean everyoneReady = true;
            for (Player player : players) {
                if (player.readyToStart() == false) {
                    everyoneReady = false;
                }
            }

            /**
             * updates the players of the current state.
             */
            UpdateGame();
            /**
             * reduces the amount of time this runs.
             */
            sleepTick();
        }

        /**
         * counts down the game before its starts
         */
        startGame();
        timer.schedule(countDown, 1000, 1000);
        lastManStanding = false;

        /**
         * start updating players
         */
        while (!paused) {
            System.out.println("running tick");

            System.out.println("serverloop started");
            //start updating players
            while (!lastManStanding) {
                //spawns new powerups
                SpawnPowerups();
                //moves powerups
                MovePowerups();
                //moves players
                playerTick();
                //send new data to players
                UpdateGame();
                //sleeps for a tick
                sleepTick();
                //if wincondition is met, cancel the while loop
                lastManStanding();
            }

            System.out.println("serverloop ended");
        }

    }
}
