package com.fuglkrig.server;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.json.*;

import javax.imageio.ImageIO;

/**
 * Created by Magnus on 03.03.2017.
 */


public class Game extends Thread {
    List<Player> players;
    List<Fugl> fugles = new ArrayList<>();
    Thread game;
    Map map;
    double timeStart = System.currentTimeMillis();
    double timeSinceLastPowerUp;
    //in millisecounds
    double timeForNewPowerUp = 5000;
    String textOnPlayerScreen;
    int secoundsUntilStart = 6;

    //Changed nr of powerups to support current count

    int numberOfPowerUps = 2;
    //used to move map and powerups. in px
    int speed;
    Random rand = new Random();
    int lastManStandingX = 0;
    int getLastManStandingY = 0;
    Player lastPlayer;
    int gameSizeX = 1920;
    int gameSizeY = 1080;
    int fugl_height = 0;
    int fugl_width = 0;
    BufferedImage fugl_image = null;

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

    public void moveLastManStanding() {

    }

    /**
     * Create a game with the list of players.
     *
     * @param players
     */
    public Game(List<Player> players) {
        this.players = players;
        this.sleepTime = 1000 / 60;
        this.lastManStanding = false;
        this.game = new Thread(this);
        this.textOnPlayerScreen = "";
        this.powerupsOnMap = new ArrayList();
        this.speed = 10;

        //creating map
        Random rand = new Random();
        //TODO NEEDS TO BE CHANGED TO 3 WHEN LAST MAP IS ADDED
        this.map = new Map(rand.nextInt(2) + 1);
        System.out.println("New game created. Map: " + map.getMapName());

    }

    public void initFugles(){
        try {
            fugl_image = ImageIO.read(new File("resources/bird.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateFugles();
    }

    public void updateFugles(){

        fugles.clear();

        fugl_height = fugl_image.getHeight();
        fugl_width = fugl_image.getWidth();

        System.out.println(fugl_image.toString());

        for(Player p: players){
            double x = p.getCoordX();
            double y = p.getCoordY();

            Fugl f = new Fugl(x,y,fugl_height,fugl_width, "resources/bird.png");

            System.out.println(f.toString());

            fugles.add(f);
        }
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

        //adds map data
        dataToPlayers.put("MapName", this.map.getMapName());
        dataToPlayers.put("CurrentMap", this.map.getCurrentMap());
        dataToPlayers.put("NextMap", this.map.getNextMap());
        dataToPlayers.put("WinMap", this.map.getWinMap());
        dataToPlayers.put("MapType", this.map.getMapType());
        dataToPlayers.put("MapXPos", this.map.getMapXPos());
        dataToPlayers.put("NextMapXPos", this.map.getNextMapXPos());
        dataToPlayers.put("WinMapXPos", this.map.getWinMapXPos());

        //liste over powerups på kart
        JSONArray powerupData = new JSONArray();
        for (Powerup powerup : powerupsOnMap) {

            //information about this object
            JSONObject powerupObject = new JSONObject();
            powerupObject.put("XPos", powerup.getX());
            powerupObject.put("YPos", powerup.getY());
            powerupObject.put("Id",powerup.getId());
            powerupObject.put("Type",powerup.type);

            //puts the object in the list
            powerupData.put(powerupObject);
        }

        //adds the powerupdata to the json that is sent to the players
        dataToPlayers.put("PowerupData", powerupData);


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
            playerData.put("PosX", (int) player.getCoordX());
            playerData.put("PosY", (int) player.getCoordY());
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
        dataToPlayers.put("Players", playersData);


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
                powerup.tick(speed);
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

        //gives the player information about the map that we are going to play
        startGame.put("MapName", this.map.getMapName());
        startGame.put("CurrentMap", this.map.getCurrentMap());
        startGame.put("NextMap", this.map.getNextMap());
        startGame.put("WinMap", this.map.getWinMap());
        startGame.put("MapType", this.map.getMapType());
        startGame.put("MapXPos", this.map.getMapXPos());
        startGame.put("NextMapXPos", this.map.getNextMapXPos());
        startGame.put("WinMapXPos", this.map.getWinMapXPos());

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

        initFugles();

    }

    public void lastManStanding() {
        int playersAlive = 0;
        for (Player player : players) {
            if (player.getAlive()) {
                playersAlive++;
            }
        }

        if (playersAlive <= 1) {
            for (Player player : players) {
                if(player.getAlive()) {
                    lastPlayer = player;
                }
            }
            lastManStanding = true;
        }
    }

    /**
     * logic for the game thread. Do not run this method. run the start() even tho it is not specified here
     */
    @Override
    public void run() {
        //todo add logic that kills the server if there isnt any players in game.

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

        /**
         * start updating players
         */
        while (!paused) {
            System.out.println("running tick");

            System.out.println("serverloop started");

            /*
            This is the game loop. When the game is started this updates all the clients untill someone won


             */
            while (!lastManStanding) {
                //moves the map
                map.moveMap(speed, lastManStanding, gameSizeX);
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
                //Updates the fugles representations on server
                updateFugles();

                //kills the server if no one is left
                if (players.size() == 0) {
                    lastManStanding = true;
                }
            }

            System.out.println("LAST MAN STANDING");

            //this is used between playing the game and lobby
            while (lastManStanding) {
                lastPlayer.targetPosX = map.getWinPosX();
                lastPlayer.targetPosY = map.getWinPosY();

                //moves the map
                map.moveMap(speed, lastManStanding, gameSizeX);
                //we still need to move the powerups.
                MovePowerups();
                //we need to control the player to the nest

                //updates the players with the new game state
                UpdateGame();
                //Still need to sleeptick to not spam the user with alot of data
                sleepTick();

                //kills the server if no one is left
                if (players.size() == 0) {
                    lastManStanding = false;
                }
            }

            if (players.size() == 0) {
                paused = true;
            }

        }

        System.out.println("Shutting down server");

    }
}
