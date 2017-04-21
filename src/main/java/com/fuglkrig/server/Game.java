package com.fuglkrig.server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.json.*;
import javax.imageio.ImageIO;

/**
 * Created by Magnus on 03.03.2017.
 */


public class Game extends Thread {
    private List<Player> players;
    private Thread game;
    private Map map;
    private double timeStart = System.currentTimeMillis();
    private double timeSinceLastPowerUp;
    //in millisecounds
    private double timeForNewPowerUp;
    private String textOnPlayerScreen;
    private int secoundsUntilStart = 5;

    //Changed nr of powerups to support current count

    private int numberOfPowerUps = 3;
    //used to move map and powerups. in px
    private int mapSpeed;
    private int wallSpeed = 8;
    private int birdpoopSpeed = 20;
    private int powerupSpeed = 10;
    private int spawn = 0;

    private Random rand = new Random();
    private int lastManStandingX = 0;
    private int getLastManStandingY = 0;
    private Player lastPlayer;
    private int gameSizeX = 1920;
    private int gameSizeY = 1080;
    private int fugl_height = 0;
    private int fugl_width = 0;
    private BufferedImage fugl_image = null;
    private BufferedImage pu0img = null;
    private BufferedImage pu1img = null;
    private BufferedImage pu2img = null;
    private BufferedImage pu3img = null;

    private double fuglScale = 2;
    private double powerupBoxScale = 2;
    private double birdpoopScale = 1.2;
    private double wallScale = 1.5;
    private int placement;

    long updateGameCurrentMillis;
    int updatesEachSecound;
    int updatesEachSecoundToClient;

    private List<Player> kickPlayer = new ArrayList<>();

    //powerups on map
    private List<Powerup> powerupsOnMap;

    //this is a count down used to(Start?)
    private Timer timer = new Timer();

    private TimerTask countDown = new TimerTask() {
        @Override
        public void run() {
            if (getSecoundsUntilStart() == 0) {
                setTextOnPlayerScreen("");
                getCountDown().cancel();
            } else {
                setTextOnPlayerScreen(Integer.toString(getSecoundsUntilStart() - 1));
            }
            setSecoundsUntilStart(getSecoundsUntilStart() - 1);
        }
    };

    private int sleepTime;

    private boolean paused;

    private boolean lastManStanding;

    /**
     * Create a game with the list of players.
     *
     * @param players
     */
    public Game(List<Player> players) {
        this.setPlayers(players);
        placement = players.size();

        for(Player p: this.players){
            System.out.println("Player in game: " + p.getNick());
        }

        this.setSleepTime(1000 / 60);
        this.setLastManStanding(false);
        this.setGame(new Thread(this));
        this.setTextOnPlayerScreen("");
        this.setPowerupsOnMap(new ArrayList());
        this.setSpeed(10);
        this.setTimeForNewPowerUp(5000);
        this.paused = false;


        InputStream is = this.getClass().getClassLoader().getResourceAsStream("bird.png");
        try {
            fugl_image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is0 = this.getClass().getClassLoader().getResourceAsStream("powerup.png");
        try {
            pu0img = ImageIO.read(is0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is1 = this.getClass().getClassLoader().getResourceAsStream("brickwall.png");
        try {
            pu1img = ImageIO.read(is1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is2 = this.getClass().getClassLoader().getResourceAsStream("brickwall.png");
        try {
            pu2img = ImageIO.read(is2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is3 = this.getClass().getClassLoader().getResourceAsStream("birdpoop.png");
        try {
            pu3img = ImageIO.read(is3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creating map
        Random rand = new Random();
        //TODO NEEDS TO BE CHANGED TO 3 WHEN LAST MAP IS ADDED
        this.setMap(new Map(rand.nextInt(3) + 1));
        System.out.println("New game created. Map: " + getMap().getMapName());
    }

    public void moveLastManStanding(Player player) {
        try {
            player.setTargetPosX(500);
            player.setTargetPosY(500);
        }
        catch(Exception e){
            System.out.println("e");
        }
    }

    public BufferedImage getPowerupImg(int type){
        switch(type){
            case 0:
                return pu0img;
            case 1:
                return pu1img;
            case 2:
                return pu2img;
            case 3:
                return pu3img;
            default:
                return null;
        }
    }

    public double getScale(Powerup p){
        switch(p.getType()){
            case 0:
                return powerupBoxScale;
            case 1:
                return wallScale;
            case 2:
                return wallScale;
            case 3:
                return birdpoopScale;
        }
        return 1;
    }

    //Remove powerups on collision

    public void powerupCollision() {

        List<Powerup> removeList = new ArrayList<>();

        for (Powerup p1 : powerupsOnMap) {
            for (Powerup p2 : powerupsOnMap) {

                double scale1 = 1;
                double scale2 = 1;

                scale1 = getScale(p1);
                scale2 = getScale(p2);

                if (CollisionDetection.powerupPowerupCollision(p1, p2, scale1, scale2) && p1 != p2) {
                        System.out.println("Powerup collision occured");

                        if (p1.getType() == 3 && p2.getType() == 3) {
                            removeList.add(p1);
                            removeList.add(p2);
                        } else if (p1.getType() == 3 && p2.getType() != 0) {
                            removeList.add(p2);
                        } else if (p2.getType() == 3 && p2.getType() != 0) {
                            removeList.add(p1);
                        } else if (p1.getType() == 0 || p2.getType() == 0) {
                            //Do nothing
                        } else {
                            removeList.add(p1);
                            removeList.add(p2);
                        }
                }
            }
        }
        getPowerupsOnMap().removeAll(removeList);
    }

    public void checkForPowerupCollisions(){
        Powerup powerup = null;
        Random rand = new Random();
        for (Powerup pUp: powerupsOnMap) {
            for (Player p: players){

                double scale = 1;

                if(pUp.getType() == 0){
                    scale = powerupBoxScale;
                }

                else if(pUp.getType() == 1 || pUp.getType() == 2){
                    scale = wallScale;
                }

                else if(pUp.getType() == 3){
                    scale = birdpoopScale;
                }


                if(CollisionDetection.playerPowerupCollision(p,pUp, fuglScale,scale) && p.isAlive()){
                    powerup=pUp;
                    try{
                        if(pUp.getType() == 0) {
                            p.addPowerUp(rand.nextInt(numberOfPowerUps)+1);
                            System.out.println("Player " + p.getNick() + " got powerup" + pUp.getType());
                            break;
                        }
                        else{
                            p.setAlive(false);
                            p.setPlacement(placement);
                            placement--;
                            System.out.println("Player " + p.getNick() + " was hit by powerup");
                        }
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                }
            }
        }
        if(powerup != null){
            powerupsOnMap.remove(powerup);
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

        updatesEachSecound++;
        if (System.currentTimeMillis() > updateGameCurrentMillis + 1000) {
            System.out.println("update game each secound: " + updatesEachSecound);
            updateGameCurrentMillis = System.currentTimeMillis();
            updatesEachSecoundToClient = updatesEachSecound;
            updatesEachSecound = 0;

        }

        boolean shouldKickPlayers = false;

        if(!kickPlayer.isEmpty()){
            shouldKickPlayers = true;
        }

        JSONObject dataToPlayers = new JSONObject();
        dataToPlayers.put("Datatype", 15);

        //adds map data
        dataToPlayers.put("MapName", this.getMap().getMapName());
        dataToPlayers.put("CurrentMap", this.getMap().getCurrentMap());
        dataToPlayers.put("NextMap", this.getMap().getNextMap());
        dataToPlayers.put("WinMap", this.getMap().getWinMap());
        dataToPlayers.put("MapType", this.getMap().getMapType());
        dataToPlayers.put("MapXPos", this.getMap().getMapXPos());
        dataToPlayers.put("NextMapXPos", this.getMap().getNextMapXPos());
        dataToPlayers.put("WinMapXPos", this.getMap().getWinMapXPos());
        dataToPlayers.put("PrintToPlayer", textOnPlayerScreen);
        dataToPlayers.put("ServerSendPackets", updatesEachSecoundToClient);

        /**
         * liste over powerups på kart
         */
        JSONArray powerupData = new JSONArray();
        for (Powerup powerup : getPowerupsOnMap()) {

            //information about this object
            JSONObject powerupObject = new JSONObject();
            powerupObject.put("XPos", powerup.getX());
            powerupObject.put("YPos", powerup.getY());
            powerupObject.put("Id",powerup.getId());
            powerupObject.put("Type", powerup.getType());

            //puts the object in the list
            powerupData.put(powerupObject);
        }

        /**
         * adds the powerupdata to the json that is sent to the players
         */
        dataToPlayers.put("PowerupData", powerupData);


        /**
         * list over players
         */
        JSONArray playersData = new JSONArray();
        for (Player player : getPlayers()) {
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


            //powerups in inventory
            JSONArray powerupList = new JSONArray(player.getPowerups());
            playerData.put("Powerups", powerupList);

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
        for (Player player : getPlayers()) {
            player.UpdateClient(dataToPlayers);
        }

        /**
         * This logic is to ensure players are notified about other platers death before removing them
         */

        if(shouldKickPlayers){
            actualKickPlayer();
        }


    }

    /**
     * Used to spawn powerups at a given time interval.
     * Adds a powerup to the powerupsOnMap list
     */
    public void SpawnPowerups() {

        setTimeSinceLastPowerUp(System.currentTimeMillis() - getTimeStart());
        if (getTimeSinceLastPowerUp() > getTimeForNewPowerUp()) {

            int x, y, height, width, type;
            x = getGameSizeX() + 100;
            y = rand.nextInt(getGameSizeY()) + 1;
            BufferedImage img = null;

            //this needs to be the same as the number of powerups.
            type = 0;
            try {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("powerup.png");
                img = ImageIO.read(is);
            } catch (IOException e) {
                System.out.println("Cant find powerup.bmp");
                System.out.println(e);
            }

            height = img.getHeight();
            width = img.getWidth();

            //int speed = 10;

            /*switch(type){
                case 0:
                    speed = powerupSpeed;
                    break;
                case 1:
                    speed = wallSpeed;
                    break;
                case 2:
                    speed = wallSpeed;
                    break;
                case 3:
                    speed = birdpoopSpeed;
                    break;
            }*/

            Powerup pu = new Powerup(x, y, height, width, type, img, powerupSpeed ,spawn);
            pu.setType(0);
            this.getPowerupsOnMap().add(pu);
            setTimeStart(System.currentTimeMillis());
            System.out.println(getPowerupsOnMap());
            System.out.println("Powerup was added");
        }
    }

    /**
     * Deploying a powerup to the game
     * @param type of powerup
     * @param x value where it is deployed
     * @param y value where it is deployed
     */
    public void addPowerup(int type, int x, int y) {
        int wallSpeed = this.wallSpeed;
        int spawn = this.spawn;

        BufferedImage img = null;

        switch (type){
            case 1:
                try {
                    wallSpeed = this.wallSpeed;
                    InputStream is = this.getClass().getClassLoader().getResourceAsStream("brickwall.png");
                    img = ImageIO.read(is);
                    spawn = -(int)(fugl_image.getWidth()*fuglScale);
                } catch (IOException e) {
                    System.out.println(e);
            }
            break;
            case 2:
                try {
                    wallSpeed = this.wallSpeed * -1;
                    InputStream is = this.getClass().getClassLoader().getResourceAsStream("brickwall.png");
                    img = ImageIO.read(is);
                    spawn = (int)(fugl_image.getWidth()*fuglScale + 50);
                } catch (IOException e) {
                    System.out.println(e);
                }
            break;
            case 3:
                try {
                    wallSpeed = this.birdpoopSpeed* -1;
                    InputStream is = this.getClass().getClassLoader().getResourceAsStream("birdpoop.png");
                    img = ImageIO.read(is);
                    spawn = (int)(fugl_image.getWidth()*fuglScale + 50);
                } catch (IOException e) {
                    System.out.println(e);
                }
                break;

        }
        /**
         * loads powerup image and adds powerups on the map
         */
        Powerup pu = new Powerup(x, y, img.getHeight(), img.getWidth(), type, img, wallSpeed, spawn);
        powerupsOnMap.remove(Integer.valueOf(type));
        powerupsOnMap.add(pu);
    }

    /**
     * Moving the powerup and removing them if they go of the screen
     */
    public void MovePowerups() {

        List<Powerup> toDelete = new ArrayList<>();
        for (Powerup powerup : getPowerupsOnMap()) {
            if (powerup.getX() < 0-(powerup.getWidth() * powerupBoxScale)) {
                toDelete.add(powerup);
            } else {
                powerup.tick();
            }
        }

        if (toDelete.size() > 0) {
            getPowerupsOnMap().removeAll(toDelete);
        }
    }

    /**
     * sleeps a game for tick time. Used to avoid a billion try/catch in the code.
     */
    public void sleepTick() {
        try {
            getGame().sleep(getSleepTime());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Is the update function of the players
     */
    public void playerTick() {
        for (Player player : getPlayers()) {
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
        startGame.put("Width", this.getGameSizeX());
        startGame.put("Height", this.getGameSizeY());

        //gives the player information about the map that we are going to play
        startGame.put("MapName", this.getMap().getMapName());
        startGame.put("CurrentMap", this.getMap().getCurrentMap());
        startGame.put("NextMap", this.getMap().getNextMap());
        startGame.put("WinMap", this.getMap().getWinMap());
        startGame.put("MapType", this.getMap().getMapType());
        startGame.put("MapXPos", this.getMap().getMapXPos());
        startGame.put("NextMapXPos", this.getMap().getNextMapXPos());
        startGame.put("WinMapXPos", this.getMap().getWinMapXPos());
        startGame.put("PlayerScale", fuglScale);
        startGame.put("BirdPoopScale", birdpoopScale);
        startGame.put("WallScale", wallScale);
        startGame.put("PowerupScale", powerupBoxScale);


        JSONArray listOfPlayers = new JSONArray();

        for (Player player : getPlayers()) {
            System.out.println("Player: " + player.getNick());
            JSONObject playerData = new JSONObject();
            playerData.put("PlayerID", player.getPlayerID());
            listOfPlayers.put(playerData);
        }

        startGame.put("Players", listOfPlayers);

        System.out.println("\n\n\n\n" + startGame + "\n\n\n\n\n");

        System.out.println(startGame);

        //sends it to players
        for (Player player : getPlayers()) {
            player.UpdateClient(startGame);
        }

    }

    /**
     * Checking if there is a win-condition
     */
    public void lastManStanding() {
        int playersAlive = 0;
        for (Player player : getPlayers()) {
            if (player.getAlive()) {
                playersAlive++;
            }
        }

        if (playersAlive <= 1) {
            for (Player player : getPlayers()) {
                if(player.getAlive()) {
                    player.setPlacement(1);
                    setLastPlayer(player);
                    moveLastManStanding(player);
                }
            }
            setLastManStanding(true);

        }
    }

    /**
     * Adding player to the kickPlayer list.
     * @param player
     */
    public void kickPlayer(Player player) {
        placement--;
        player.setAlive(false);
        kickPlayer.add(player);
    }

    /**
     * Remoing all the players thats in kickPlayer list from the game
     */
    private void actualKickPlayer() {
        players.removeAll(kickPlayer);
    }

    /**
     * Returning information to the client when the game is over.
     */
    private void gameOver() {
        JSONObject endGame = new JSONObject();
        endGame.put("Datatype", 16);
        endGame.put("PrintToPlayer", "Game Over");

        for (Player player: players) {

            boolean won = false;

            if(player.getPlacement() == 1){
                won = true;
            }

            endGame.put("Victory", won);
            endGame.put("Placement",player.getPlacement());
            player.UpdateClient(endGame);
        }
    }

    /**
     * logic for the game thread. Do not run this method. run the start() even tho it is not specified here
     */
    @Override
    public void run() {
        System.out.println("gameloop started");

        //give random coordinates to the players
        for (Player player: players) {
            player.setCoordX(rand.nextInt(gameSizeX - 50));
            player.setTargetPosX(rand.nextInt(gameSizeX - 50));
            player.setCoordY(rand.nextInt(gameSizeY - 50));
            player.setTargetPosY(rand.nextInt(gameSizeY - 50));
        }

        startGame();
        System.out.println("running startgame");

        /**
         * counts down the game before its starts
         */
        boolean readyToStart = false;
        double getStartMilis = System.currentTimeMillis();
        while (!readyToStart) {
            //checks if we should decrease the timer and prints new timer to player
            if (System.currentTimeMillis() > getStartMilis + 1000) {
                secoundsUntilStart--;
                System.out.println("decreasing timer");
                getStartMilis = System.currentTimeMillis();
            }

            //breaks the timer and starts the next phase
            textOnPlayerScreen = Integer.toString(secoundsUntilStart);
            if (secoundsUntilStart == 0) {
                textOnPlayerScreen = "";
                readyToStart = true;
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

        System.out.println("exited timer");


        /**
         * start updating players
         */
        while (!paused) {
            /**
             * This is the game loop. When the game is started this updates all the clients until someone won
             */
            while (!isLastManStanding()) {
                //moves the map
                getMap().moveMap(getSpeed(), isLastManStanding(), getGameSizeX());

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
                //Check for collisions between fugles
                checkForPowerupCollisions();

                //Checks for collision between powerups
                powerupCollision();

                //kick players waiting to be kicked

                //kills the server if no one is left
                if (getPlayers().size() == 0) {
                    System.out.println("Setting last man standing true");
                    setLastManStanding(true);
                }
            }

            System.out.println("LAST MAN STANDING");


            getStartMilis = System.currentTimeMillis();
            //this is used between playing the game and lobby
            while (isLastManStanding()) {
                getLastPlayer().setTargetPosX(getMap().getWinPosX());
                getLastPlayer().setTargetPosY(getMap().getWinPosY());

                //moves the map
                map.moveMap(getSpeed(), isLastManStanding(), getGameSizeX());
                //we still need to move the powerups.
                MovePowerups();
                //we need to control the player to the nest

                //updates the players with the new game state
                UpdateGame();
                //Still need to sleeptick to not spam the user with alot of data
                sleepTick();

                //kick players waiting to be kicked
                actualKickPlayer();

                //kills the server if no one is left
                if (players.size() == 0) {
                    setLastManStanding(false);
                    System.out.println("Last man standing false");
                }

                if (getStartMilis < System.currentTimeMillis() + 5000) {
                    lastManStanding = false;
                    System.out.println("Last man standing false");
                }

            }
            paused = true;
            
            System.out.println("Sending game over");
            gameOver();

            if (getPlayers().size() == 0) {
                setPaused(true);
            }
        }

        System.out.println("Shutting down server");

    }

    /**
     * Getters and setters list for the game.
     */

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Thread getGame() {
        return game;
    }

    public void setGame(Thread game) {
        this.game = game;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(double timeStart) {
        this.timeStart = timeStart;
    }

    public double getTimeSinceLastPowerUp() {
        return timeSinceLastPowerUp;
    }

    public void setTimeSinceLastPowerUp(double timeSinceLastPowerUp) {
        this.timeSinceLastPowerUp = timeSinceLastPowerUp;
    }

    public double getTimeForNewPowerUp() {
        return timeForNewPowerUp;
    }

    public void setTimeForNewPowerUp(double timeForNewPowerUp) {
        this.timeForNewPowerUp = timeForNewPowerUp;
    }

    public String getTextOnPlayerScreen() {
        return textOnPlayerScreen;
    }

    public void setTextOnPlayerScreen(String textOnPlayerScreen) {
        this.textOnPlayerScreen = textOnPlayerScreen;
    }

    public int getSecoundsUntilStart() {
        return secoundsUntilStart;
    }

    public void setSecoundsUntilStart(int secoundsUntilStart) {
        this.secoundsUntilStart = secoundsUntilStart;
    }

    public int getNumberOfPowerUps() {
        return numberOfPowerUps;
    }

    public void setNumberOfPowerUps(int numberOfPowerUps) {
        this.numberOfPowerUps = numberOfPowerUps;
    }

    public int getSpeed() {
        return mapSpeed;
    }

    public void setSpeed(int speed) {
        this.mapSpeed = speed;
    }


    public int getLastManStandingX() {
        return lastManStandingX;
    }

    public void setLastManStandingX(int lastManStandingX) {
        this.lastManStandingX = lastManStandingX;
    }

    public int getGetLastManStandingY() {
        return getLastManStandingY;
    }

    public void setGetLastManStandingY(int getLastManStandingY) {
        this.getLastManStandingY = getLastManStandingY;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public int getGameSizeX() {
        return gameSizeX;
    }

    public void setGameSizeX(int gameSizeX) {
        this.gameSizeX = gameSizeX;
    }

    public int getGameSizeY() {
        return gameSizeY;
    }

    public void setGameSizeY(int gameSizeY) {
        this.gameSizeY = gameSizeY;
    }

    public int getFugl_height() {
        return fugl_height;
    }

    public void setFugl_height(int fugl_height) {
        this.fugl_height = fugl_height;
    }

    public int getFugl_width() {
        return fugl_width;
    }

    public void setFugl_width(int fugl_width) {
        this.fugl_width = fugl_width;
    }

    public BufferedImage getFugl_image() {
        return fugl_image;
    }

    public void setFugl_image(BufferedImage fugl_image) {
        this.fugl_image = fugl_image;
    }

    public List<Powerup> getPowerupsOnMap() {
        return powerupsOnMap;
    }

    public void setPowerupsOnMap(List<Powerup> powerupsOnMap) {
        this.powerupsOnMap = powerupsOnMap;
    }

    public void removePowerupOnMap(Powerup powerup){
        powerupsOnMap.remove(powerup);
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public TimerTask getCountDown() {
        return countDown;
    }

    public void setCountDown(TimerTask countDown) {
        this.countDown = countDown;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    /**
     * how many times a secound the game should update
     * 1000 / tick = time between updates
     */
    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setLastManStanding(boolean lastManStanding) {
        this.lastManStanding = lastManStanding;
    }


    /**
     * used to make sure everyone has loaded the game and is ready to start
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * used to check if there is a winner of the game
     */
    public boolean isLastManStanding() {
        return lastManStanding;
    }


}
