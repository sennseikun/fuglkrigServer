package com.fuglkrig.server;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import com.fuglkrig.server.interfaces.EntityPowerUp;

/**
 * Created by Tore on 20.03.2017.
 */
public class Powerup extends GameObject implements EntityPowerUp {

    private int type;
    private String powerUpName;
    private int damage;
    private int speed;
    private int spawn;

    private HashSet<String> mask = new HashSet<>();

    private BufferedImage img = null;

    public Powerup(int x, int y, int height, int width, int type, BufferedImage img, int speed, int spawn){
        super((x + spawn), y, width, height);
        this.setType(type);
        this.setImg(img);
        this.damage = 1;
        this.speed = speed;
        this.spawn = spawn;

        /**
         * TODO fix this to give powers!. MATCH THIS WITH CLIENT
         * remember to fix Powerup! It has a randint that needs to be changed
         */
        switch (this.getType()) {

            case 0: setPowerUpName("Undeployed");
                break;
            case 1: setPowerUpName("Walls Forwards");
                break;
            case 2: setPowerUpName("Walls Up");
                break;
            case 3: setPowerUpName("Walls Down");
                break;
            case 4: setPowerUpName("Walls Back");
                break;
            case 5: setPowerUpName("Birdpoop");
                break;
            case 6: setPowerUpName("SpeedUp");
                break;
            case 7: setPowerUpName("SpeedDown");
                break;
            case 8: setPowerUpName("Fake powerup");
                break;
            default:
                System.out.println("\n\n\nWARNING!\nThe randint in Powerup doesnt match the cases in the other Powerup constructor in the powerup file.\n\n\n ");
        }
        makeHashSet();
    }

    /**
     * update function for the powerup
     */
    public void tick() {
        setX(super.getX() - (this.speed * 1));
    }

    /**
     * Setting the id of the powerup
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the id of the powerup
     */
    public int getId() {
        return super.getId();
    }

    /**
     * Creates an rectangle around the powerup
     * @return the rectangle
     */
    public Rectangle getBounds(double scale) {
        return super.getBounds(scale);
    }

    /**
     * @return the powerups x value
     */
    public int getX() {
        return super.getX();
    }

    /**
     * @return the powerups y value
     */
    public int getY() {
        return super.getY();
    }

    public int getType() {
        return type;
    }

    public String getPowerUpName() {
        return powerUpName;
    }

    public void setPowerUpName(String powerUpName) {
        this.powerUpName = powerUpName;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    /**
     * Create a hashSet of the image of the powerup
     */
    private void makeHashSet(){
        BufferedImage image;
        try {

            image = getImg();
            int pixel, a;
            for(int i = 0; i < image.getWidth(); i++){ // for every (x,y) component in the given box,
                for( int j = 0; j < image.getHeight(); j++){

                    pixel = image.getRGB(i, j); // get the RGB value of the pixel
                    a= (pixel >> 24) & 0xff;

                    if(a != 0){  // if the alpha is not 0, it must be something other than transparent
                        mask.add((getX()+i)+","+(getY()- j)); // add the absolute x and absolute y coordinates to our set
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    public HashSet<String> getMask(){
        return mask;  //return our set
    }
}
