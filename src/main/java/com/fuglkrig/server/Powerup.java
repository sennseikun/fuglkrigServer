package com.fuglkrig.server;


import java.awt.*;
import java.awt.image.BufferedImage;

import com.fuglkrig.server.classes.EntityPowerUp;

/**
 * Created by Tore on 20.03.2017.
 */
public class Powerup extends GameObject implements EntityPowerUp {

    private int type;
    private String powerUpName;
    private int damage;

    private BufferedImage img = null;

    public Powerup(int x, int y, int height, int width, int type, BufferedImage img){
        super(x, y, width, height);
        this.setType(type);
        this.setImg(img);
        this.damage = 1;

        //TODO fix this to give powers!
        //remember to fix Powerup! It has a randint that needs to be changed
        switch (this.getType()) {

            case 0: setPowerUpName("Undeployed");
                break;
            case 1: setPowerUpName("Wall Forwards");
                break;
            case 2: setPowerUpName("Wall Up");
                break;
            case 3: setPowerUpName("Wall Down");
                break;
            case 4: setPowerUpName("Wall Back");
                break;
            case 5: setPowerUpName("Cannonball");
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
    }

    /**
     * update function for the powerup
     */
    public void tick(int speed) {
        setX(super.getX() - speed);
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
    public Rectangle getBounds() {
        return super.getBounds();
    }

    /**
     * @return the powerups x value
     */
    public double getX() {
        return super.getX();
    }

    /**
     * @return the powerups y value
     */
    public double getY() {
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
}
