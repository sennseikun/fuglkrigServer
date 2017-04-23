package com.fuglkrig.server;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Magnus on 03.03.2017.
 */
public class Player extends CollisionDetection {
    private String nick;
    private int playerID;
    private int hp;
    private int skin;
    private double targetPosX;
    private double targetPosY;
    private double coordX;
    private double coordY;
    private double xSpeed;
    private double ySpeed;
    private double direction;
    private double speed;
    private boolean alive;
    private int placement = -1;

    private Socket playerSocket;

    private ArrayList<Integer> powerups = new ArrayList<>();
    private Game currentGame;
    private double dx;
    private double dy;
    private BufferedImage fugl_image;
    private int width = 0;
    private int height = 0;
    private HashSet<String> mask = new HashSet<>();
    private String unique_id;




    /**
     * Changes the toString to return the name of the player.
     * @return the name of the player
     */
    @Override
    public String toString(){
        return getNick();
    }

    /**
     * Create a player with these param
     * @param nick
     * @param playerID
     * @param skin
     * @param connection
     */
    public Player(String nick, int playerID,String unique_id, int skin, Socket connection) {
        this.setNick(nick);
        this.setPlayerID(playerID);
        this.setSkin(skin);
        this.setPlayerSocket(connection);
        this.setAlive(true);
        this.setTargetPosX(1);
        this.setTargetPosY(1);
        this.coordX = 5;
        this.coordY = 5;
        this.setxSpeed(5);
        this.setySpeed(5);
        this.unique_id = unique_id;

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("bird.png");
            fugl_image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.width = fugl_image.getWidth();
        this.height = fugl_image.getHeight();
        makeHashSet();
    }

    /**
     * Change to try and better collision detection
     * @param scale
     * @return rectangle of the players
     */

    public Rectangle getRectangle(double scale){
        return new Rectangle((int)this.getCoordX(),(int)this.getCoordY(),(int)(45*scale),(int)(this.getHeight()*scale));
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
     * Create a hashSet of the image of the player
     */
    private void makeHashSet(){

        BufferedImage image;
        try {
            image = getFugl_image();
            int pixel, a;
            for(int i = 0; i < image.getWidth(); i++){ // for every (x,y) component in the given box,
                for( int j = 0; j < image.getHeight(); j++){

                    pixel = image.getRGB(i, j); // get the RGB value of the pixel
                    a= (pixel >> 24) & 0xff;

                    if(a != 0){  // if the alpha is not 0, it must be something other than transparent
                        mask.add(((int)getCoordX()+i)+","+((int)getCoordY()- j)); // add the absolute x and absolute y coordinates to our set
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
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

            System.out.println(getPlayerSocket());
            System.out.println(sendJson);

            DataOutputStream out = new DataOutputStream(getPlayerSocket().getOutputStream());
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
            if(LobbyList.getLobby(lobbyID).getPlayers().size() > 1 && LobbyList.getLobby(lobbyID).getPlayers().get(0).getNick().equals(name)){
                sendJson.put("hostPlayer",LobbyList.getLobby(lobbyID).getPlayers().get(1).getNick());
            }
            else{
                sendJson.put("hostPlayer",LobbyList.getLobby(lobbyID).getPlayers().get(0).getNick());
            }

            sendJson.put("PlayerCount",Integer.toString(players.size()-1));

            sendJson.put("PlayerName",name);

            DataOutputStream out = new DataOutputStream(getPlayerSocket().getOutputStream());
            out.writeUTF(sendJson.toString());

            System.out.println("Sent removing update to player: "+getNick());
        }catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * to be able do to communication we need the socket here
     */ /**
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
    public double getTargetPosX() {
        return this.targetPosX;
    }

    /**
     * @return the players target y position
     */
    public double getTargetPosY() {
        return this.targetPosY;
    }

    /**
     * @return the players current x position
     */
    public double getCoordX() {
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

    public void setTargetPosX(double x){
        targetPosX = x;
    }

    /**
     * Updates the direction in the x and y axis
     */
    public void UpdateDxDy(){
        setDx(getTargetPosX() - getCoordX());
        setDy(getTargetPosY() - getCoordY());
        float targetPosLength = (float) Math.sqrt(getDx() * getDx() + getDy() * getDy());
        this.setxSpeed(getDx() /targetPosLength);
        this.setySpeed(getDy() /targetPosLength);
    }

    public void setTargetPosY(double y){
        targetPosY = y;
    }


    public double getCoordY() {
        return this.coordY;
    }

    /**
     * @return the players direction
     */
    public double getDirection() {
        return this.direction;
    }

    /**
     * @return the players current speed
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * @return if the player is alive or not
     */
    public boolean getAlive() {
        return this.isAlive();
    }

    /**
     * @return a json of the players powerup
     */
    public ArrayList<Integer> getPowerups() {
        return this.powerups;
    }


    /**
     * Adding powerup to the player
     * @param powerUp
     */
    public void addPowerUp(int powerUp){
        System.out.println("player got powerup of type " + powerUp);
        try {
            this.powerups.clear();
            this.powerups.add(powerUp);
        }catch (Exception e) {
            System.out.println(e);
        }
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
        this.setCurrentGame(game);
    }

    /**
     * todo fix this comment
     * Update function for the changing of direction of the player.
     */
    public void nextTick() {


        if ((getDx() > 0 && getCoordX() >= getTargetPosX()) || (getDx() < 0 && getCoordX() <= getTargetPosX())){
            this.setxSpeed(0);
        }
        if ((getDy() > 0 && getCoordY() >= getTargetPosY()) || (getDy() < 0 && getCoordY() <= getTargetPosY())){
            this.setySpeed(0);
        }

        this.setCoordX(this.getCoordX() + getxSpeed() * 15);
        this.setCoordY(this.getCoordY() + getySpeed() * 15);
    }


    /**
     * make all game specific variables something that doesnt make sence. like negatives.
     */
    public void notInGame() {
        this.setHp(-1);
        this.setTargetPosY(-1);
        this.setTargetPosX(-1);
        this.coordX = -1;
        this.coordY = -1;
        this.setDirection(-1);
        this.setSpeed(-1);
        this.setAlive(false);
        this.powerups.clear();
        this.setCurrentGame(null);
    }

    /**
     * send the data that comes from the game out to the client
     */
    public void UpdateClient(JSONObject data) {

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(getPlayerSocket().getOutputStream());
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
        this.setNick(nick);
        this.setSkin(skin);
    }

    /**
     * Checks if the player is ready to start
     * @return false until the player is ready to start
     */
    public boolean readyToStart() {
    	return true;
    }

    /**
     * @return the width of the player.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the player.
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height of the player.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the player.
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the nick of the player.
     * @param nick
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * Sets the player ID
     * @param playerID
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Sets the HP of the player.
     * @param hp
     */
    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * Sets the skin of the player.
     * @param skin
     */
    public void setSkin(int skin) {
        this.skin = skin;
    }

    /**
     * Sets the coordinate of the player in the x axis.
     * @param coordX
     */
    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    /**
     * Sets the coordinate of the player in the y axis.
     * @param coordY
     */
    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    /**
     * @return the speed in the x axis.
     */
    public double getxSpeed() {
        return xSpeed;
    }

    /**
     * Sets the speed in the x axis.
     * @param xSpeed
     */
    public void setxSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    /**
     * @return the speed in the y axis.
     */
    public double getySpeed() {
        return ySpeed;
    }

    /**
     * Sets the speed in the y axis.
     * @param ySpeed
     */
    public void setySpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    /**
     * Sets the direction of the player.
     * @param direction
     */
    public void setDirection(double direction) {
        this.direction = direction;
    }

    /**
     * Sets the speed of the player.
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * @return the boolean value if the player is alive.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets the socket of the player.
     * @param playerSocket
     */
    public void setPlayerSocket(Socket playerSocket) {
        this.playerSocket = playerSocket;
    }

    /**
     * Sets the powerups the player has.
     * @param powerups
     */
    public void setPowerups(ArrayList<Integer> powerups) {
        this.powerups = powerups;
    }

    /**
     * @return the current game of the player.
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Sets the current game of the player.
     * @param currentGame
     */
    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    /**
     * @return the direction in the x axis.
     */
    public double getDx() {
        return dx;
    }

    /**
     * Sets the direction in the x axis.
     * @param dx
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * @return the direction in the y axis.
     */
    public double getDy() {
        return dy;
    }

    /**
     * Sets the direction in the y axis.
     * @param dy
     */
    public void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * @return the image of the bird.
     */
    public BufferedImage getFugl_image(){
        return fugl_image;
    }

    /**
     * @return the hashSet of the image.
     */
    public HashSet<String> getMask(){
        return mask;  //return our set

    }

    /**
     * @return the placement of the player.
     */
    public int getPlacement() {
        return placement;
    }

    /**
     * Sets the placement of the player.
     * @param placement
     */
    public void setPlacement(int placement) {
        this.placement = placement;
    }

    /**
     * @return the unique id of the player.
     */
    public String getUnique_id() {
        return unique_id;
    }

    /**
     * Sets the unique id of the player.
     * @param unique_id
     */
    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }
}
